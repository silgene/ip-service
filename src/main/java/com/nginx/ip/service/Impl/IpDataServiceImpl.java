package com.nginx.ip.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import com.nginx.ip.config.AliConfig;
import com.nginx.ip.constant.RegionConstants;
import com.nginx.ip.mapper.IpDataMapper;
import com.nginx.ip.pojo.entity.IpData;
import com.nginx.ip.service.IpDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IpDataServiceImpl implements IpDataService {

    @Autowired
    private AliConfig aliConfig;

    @Autowired
    private IpDataMapper ipDataMapper;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 保存或更新用户的 IP 数据
     *
     * @param clientIp 客户端 IP
     */
    @Override
    public void saveOrUpdateIpData(String clientIp, String url) throws Exception {
        log.info("收到ip地址: {}", clientIp);
        LocalDateTime currentDateTime = LocalDateTime.now();

        // ip,url,date 确定唯一的一行
        QueryWrapper<IpData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip_address", clientIp);
        queryWrapper.eq("url", url);
        queryWrapper.between("date", currentDateTime.toLocalDate().atStartOfDay(), currentDateTime.toLocalDate().atTime(23, 59, 59));

        IpData existingIpData = ipDataMapper.selectOne(queryWrapper);
        // 更新访问次数和更新时间字段
        if (existingIpData != null) {
            existingIpData.setAccessCount(existingIpData.getAccessCount() + 1);
            existingIpData.setUpdateTime(LocalDateTime.now());
            ipDataMapper.updateById(existingIpData);
        } else {
            IpData newIpData = createIpData(clientIp, url);
            ipDataMapper.insert(newIpData);
        }
    }

    /**
     * 创建新的 IpData 对象，远程调用阿里云API获取 IP 地址信息
     *
     * @param clientIp  客户端 IP
     * @param originURL 原始 URL
     * @return
     * @throws Exception
     */
    private IpData createIpData(String clientIp, String originURL) throws Exception {
        IpData newIpData = new IpData();

        // 阿里云 API 端点
        String url = aliConfig.getUrl();
        String appCode = aliConfig.getAppcode();

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + appCode);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("ip", clientIp);

        // 发送 API 请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        JsonNode data = jsonResponse.get("data");

        // 提取数据
        String region = data.get("region").asText();
        String country = data.get("country").asText();
        String city = data.get("city").asText();

        // 从 RegionConstants 获取标准化地区名称，只有中国需要修改省份
        if (country.equals("中国")) {
            region = RegionConstants.REGION_MAPPING.getOrDefault(region, region + "省");
        } else if (country.equals("保留")) {
            country = "中国";
            region = "广东省";
            city = "深圳";
        }
        // 存储数据
        newIpData.setIpAddress(clientIp);
        newIpData.setRegionName(region);
        newIpData.setCountry(country);
        newIpData.setCity(city);
        newIpData.setDate(LocalDateTime.now());
        newIpData.setUpdateTime(LocalDateTime.now());
        newIpData.setAccessCount(1);
        newIpData.setUrl(originURL); // 设置原始URL

        return newIpData;
    }


    /**
     * 目前暂时没有使用，暂时留着 TODO
     * 获取指定时间范围内的 IP 数据
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public List<IpData> getIpDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<IpData> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("date", startTime, endTime);
        return ipDataMapper.selectList(queryWrapper);
    }

    /**
     * 获取指定时间范围内的省份数据（精度到省份）
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return
     */
    private List<IpData> getProvinceDataByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return ipDataMapper.getGroupedDataByRegion(startTime, endTime);
    }

    /**
     * 生成并保存各个省份的词云图，并保存到本地
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @description 注：这里使用到Noto Sans CJK TC字体，避免中文乱码
     * 如果生成的图片中有乱码，请确保服务器中安装了该字体
     * 如果没有该字体，可自行下载或者切换其他已有的中文字体
     */
    public String generateAndSaveWordCloud(LocalDateTime startTime, LocalDateTime endTime) {
        // 获取省份数据
        List<IpData> ipDataList = getProvinceDataByTimeRange(startTime, endTime);

        List<WordFrequency> wordFrequencies = ipDataList.stream()
                .map(ipData -> new WordFrequency(ipData.getRegionName(), ipData.getAccessCount()))
                .collect(Collectors.toList());

        // 设置图片大小
        Dimension dimension = new Dimension(600, 600);

        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // 设置中文字体（避免乱码）
        Font font = new Font("Noto Sans CJK TC", Font.BOLD, 20);
        wordCloud.setKumoFont(new KumoFont(font));

        // 设置边距
        wordCloud.setPadding(2);

        // 配色更自然：渐变颜色更有层次
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));

        // 设置词云字体大小范围
        wordCloud.setFontScalar(new SqrtFontScalar(15, 50));

        // 设置为圆形背景，更美观
        wordCloud.setBackground(new CircleBackground(200));

        // 设置背景颜色为白色
        wordCloud.setBackgroundColor(Color.WHITE);

        // 生成词云
        wordCloud.build(wordFrequencies);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/circle.png");
        // 使用图片轮廓控制词云形状（黑白图，白色区域为词区域）
        try {
            wordCloud.setBackground(new PixelBoundryBackground(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 保存路径
        String fileName = generateFileName(startTime, endTime, "province-word-cloud");
        String directory = "chart_images";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File outputFile = new File(directory, fileName + ".png");
        wordCloud.writeToFile(outputFile.getAbsolutePath());

        log.info("云图已保存到: {}", outputFile.getAbsolutePath());
        return fileName + ".png";
    }


    /**
     * 生成中国热力图，并保存到本地
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @throws Exception
     */
    public String generateChinaHeatMap(LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        // 查询数据库，筛选 country 为 "中国" 的数据，并按 region_name 分组
        List<IpData> ipDataList = getProvinceDataByTimeRange(startTime, endTime).stream()
                .filter(ipData -> "中国".equals(ipData.getCountry()))
                .collect(Collectors.toList());

        // 按 region_name 分组统计访问次数
        Map<String, Integer> provinceData = ipDataList.stream()
                .collect(Collectors.groupingBy(IpData::getRegionName, Collectors.summingInt(IpData::getAccessCount)));

        // 生成热力图
        String fileName = generateFileName(startTime, endTime, "china-heatmap");

        // 使用ClassLoader加载SVG资源
        try (InputStream svgStream = getClass().getClassLoader().getResourceAsStream("static/china.svg")) {
            if (svgStream == null) {
                throw new RuntimeException("无法找到中国地图SVG资源文件: static/china.svg");
            }

            // 确保输出目录存在
            String outputDirectory = "chart_images";
            File dir = new File(outputDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String outputFilePath = outputDirectory + File.separator + fileName + ".png";
            generateProvinceHeatMap(provinceData, svgStream, outputFilePath);

            return fileName + ".png";
        }
    }

    /**
     * 获取月活跃用户（去重）
     */
    @Override
    public int getMonthlyActiveUsers(LocalDateTime startTime, LocalDateTime endTime) {
        return ipDataMapper.getMonthlyActiveUsers(startTime, endTime);
    }

    /**
     * 获取日活跃用户（去重）
     */
    @Override
    public int getDailyActiveUsers(LocalDateTime date) {
        return ipDataMapper.getDailyActiveUsers(date);
    }

    /**
     * 计算七日留存率
     */
    @Override
    public double getSevenDayRetentionRate(LocalDateTime startTime, LocalDateTime endTime) {
        int activeUsers = ipDataMapper.getSevenDayRetentionBase(startTime.minusDays(7), startTime);
        log.info("过去 7 天的活跃用户数: {}", activeUsers);
        int newUsers = ipDataMapper.getSevenDayRetentionBase(startTime, endTime);
        log.info("区间内的新用户数: {}", newUsers);
        // 七日留存率 ： (过去 7 天的活跃用户数) / (区间内的新用户数)，返回值为结果，不为百分比
        return newUsers == 0 ? 0.0 : (double) activeUsers / newUsers;
    }

    /**
     * 获取总浏览量（PV）
     */
    @Override
    public int getTotalPageViews(LocalDateTime startTime, LocalDateTime endTime) {
        return ipDataMapper.getTotalPageViews(startTime, endTime);
    }

    /**
     * 生成省份热力图
     *
     * @param provinceData   省份数据
     * @param svgStream      SVG 文件输入流
     * @param outputFilePath 输出文件路径
     * @throws Exception
     */
    private void generateProvinceHeatMap(Map<String, Integer> provinceData, InputStream svgStream, String outputFilePath) throws Exception {
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());

        // 从InputStream加载SVG文件
        Document svgDocument = factory.createDocument(XMLResourceDescriptor.getXMLParserClassName(), svgStream);

        int maxCount = provinceData.values().stream().max(Integer::compareTo).orElse(1);

        for (Map.Entry<String, Integer> entry : provinceData.entrySet()) {
            String provinceId = entry.getKey();
            int count = entry.getValue();

            // 计算颜色填充强度
            float intensity = (float) Math.sqrt((double) count / maxCount);

            // 生成红色渐变颜色
            Color color = new Color(1.0f, 1.0f - intensity, 1.0f - intensity);

            log.info("当前处理的省份ID: {} 访问量: {}", provinceId, count);

            Element provinceElement = svgDocument.getElementById(provinceId);
            if (provinceElement != null) {
                String fillColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                provinceElement.setAttribute("fill", fillColor);
            }
        }

        // 转换为 PNG
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(svgDocument);
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);
            log.info("PNG 文件生成成功: {}", outputFilePath);
        } catch (Exception e) {
            log.error("PNG 转换失败: {}", e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 生成各个省份的柱状图，并保存到本地
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     */
    @Override
    public String generateAndSaveProvinceBarChart(LocalDateTime startTime, LocalDateTime endTime) {
        // 获取指定时间范围内的省份数据
        List<IpData> ipDataList = getProvinceDataByTimeRange(startTime, endTime);

        // 横坐标是省份，纵坐标是访问次数
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (IpData ipData : ipDataList) {
            dataset.addValue(ipData.getAccessCount(), "访问次数", ipData.getRegionName());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "按省份统计的访问次数", // 图表标题
                "省份", // 横坐标
                "访问次数", // 纵坐标
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true,
                true,
                false
        );

        // 设置标题字体
        barChart.getTitle().setFont(new Font("Noto Sans CJK TC", Font.BOLD, 18));

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        // 设置坐标轴字体
        plot.getDomainAxis().setLabelFont(new Font("Noto Sans CJK TC", Font.PLAIN, 16));
        plot.getDomainAxis().setTickLabelFont(new Font("Noto Sans CJK TC", Font.PLAIN, 16));
        plot.getDomainAxis().setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6) // 30度旋转
        );
        plot.getRangeAxis().setLabelFont(new Font("Noto Sans CJK TC", Font.PLAIN, 16));

        // 设置柱子的颜色
        plot.getRenderer().setSeriesPaint(0, new Color(79, 129, 189));

        // 设置数据标签
        plot.getRenderer().setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        plot.getRenderer().setDefaultItemLabelsVisible(true);
        plot.getRenderer().setDefaultItemLabelFont(new Font("Noto Sans CJK TC", Font.PLAIN, 12));

        // 设置图例字体
        if (barChart.getLegend() != null) {
            barChart.getLegend().setItemFont(new Font("Noto Sans CJK TC", Font.PLAIN, 12));
        }

        // 设置固定的柱子宽度
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.05);  // 设置最大柱子宽度比例，0.05 表示宽度占整体图表的 5%
        renderer.setMinimumBarLength(0.1);  // 设置最小柱子长度，避免出现宽度过小的柱子

        String fileName = generateFileName(startTime, endTime, "province-bar-chart");
        saveChartToFile(barChart, fileName); // 保存图表到文件

        // 返回图表的相对路径
        return fileName + ".png";
    }

    /**
     * 保存图表到文件中
     *
     * @param chart    图表对象
     * @param fileName 文件名
     */
    private void saveChartToFile(JFreeChart chart, String fileName) {
        String directory = "chart_images";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File chartFile = new File(directory, fileName + ".png");
        try (FileOutputStream fileOutputStream = new FileOutputStream(chartFile)) {
            org.jfree.chart.ChartUtils.writeChartAsPNG(fileOutputStream, chart, 800, 600);
            log.info("图表已保存到: {}", chartFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存图表失败", e);
        }
    }

    /**
     * 生成文件名
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @param chartType 图表类型
     * @return
     */
    private String generateFileName(LocalDateTime startTime, LocalDateTime endTime, String chartType) {
        String startDate = startTime.toLocalDate().toString();
        String endDate = endTime.toLocalDate().toString();
        return startDate + "_to_" + endDate + "_" + chartType;
    }
}
