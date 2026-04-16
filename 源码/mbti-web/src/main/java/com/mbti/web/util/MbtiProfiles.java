package com.mbti.web.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MbtiProfiles {
  private MbtiProfiles() {}

  private static final Pattern TYPE_PATTERN = Pattern.compile("(?i)([EI][NS][TF][JP])");

  public static final class Profile {
    private final String type;
    private final String title;
    private final String summary;
    private final List<String> strengths;
    private final List<String> tips;
    private final List<String> careers;

    public Profile(String type, String title, String summary, List<String> strengths, List<String> tips, List<String> careers) {
      this.type = type;
      this.title = title;
      this.summary = summary;
      this.strengths = strengths == null ? Collections.emptyList() : strengths;
      this.tips = tips == null ? Collections.emptyList() : tips;
      this.careers = careers == null ? Collections.emptyList() : careers;
    }

    public String getType() {
      return type;
    }

    public String getTitle() {
      return title;
    }

    public String getSummary() {
      return summary;
    }

    public List<String> getStrengths() {
      return strengths;
    }

    public List<String> getTips() {
      return tips;
    }

    public List<String> getCareers() {
      return careers;
    }
  }

  private static Profile p(String type, String title, String summary, String[] strengths, String[] tips, String[] careers) {
    return new Profile(type, title, summary,
        strengths == null ? Collections.emptyList() : Arrays.asList(strengths),
        tips == null ? Collections.emptyList() : Arrays.asList(tips),
        careers == null ? Collections.emptyList() : Arrays.asList(careers));
  }

  private static final Map<String, Profile> PROFILES;
  static {
    Map<String, Profile> m = new HashMap<>();

    // 说明：以下为本系统自定义的简要描述（非引用外部材料），用于丰富结果页展示。
    m.put("ISTJ", p("ISTJ", "务实的秩序维护者",
        "重视规则与可靠性，做事讲流程、讲证据，倾向把事情按计划落地。",
        new String[]{"踏实负责，执行力强", "注重细节，风险意识强", "稳定耐心，守承诺"},
        new String[]{"给自己留出弹性空间，避免过度僵化", "适度表达感受与需求", "在变化中先抓住关键目标"},
        new String[]{"质量/流程管理", "财务审计", "项目/运维", "行政管理"}));

    m.put("ISFJ", p("ISFJ", "温和的守护者",
        "关注他人感受与实际需要，愿意默默承担责任，让团队运转更顺畅。",
        new String[]{"细致体贴，服务意识强", "可靠守时，注重承诺", "擅长维护稳定的关系"},
        new String[]{"学会拒绝，避免过度消耗", "把“该做”与“想做”分清", "遇到冲突用事实+感受一起表达"},
        new String[]{"护理/健康管理", "人力/行政", "客户支持", "教育助理"}));

    m.put("INFJ", p("INFJ", "洞察的引导者",
        "善于从人和事背后看到动机与趋势，重视意义感，愿意用温和方式影响他人。",
        new String[]{"洞察力强，善于理解他人", "有使命感，能长期投入", "表达有感染力"},
        new String[]{"把理想拆成可执行的小目标", "避免过度共情导致内耗", "给自己安排独处恢复时间"},
        new String[]{"心理/咨询", "内容策划", "产品经理", "教育培训"}));

    m.put("INTJ", p("INTJ", "战略的规划者",
        "习惯用系统思维分析问题，追求效率与长期最优解，喜欢独立推进复杂事项。",
        new String[]{"逻辑强，结构化思考", "自驱力强，擅长规划", "抗干扰，目标导向"},
        new String[]{"多做阶段性沟通，减少“信息不对称”", "在关键节点引入反馈", "别把效率当成唯一标准"},
        new String[]{"研发/架构", "数据分析", "战略/咨询", "产品与项目管理"}));

    m.put("ISTP", p("ISTP", "冷静的解决者",
        "偏好用动手与实验找到答案，遇到问题先拆解再修复，讲求可用与效率。",
        new String[]{"动手能力强，善于排障", "临场冷静，适应变化", "关注事实与结果"},
        new String[]{"在团队中多同步思路与进度", "留意他人的情绪信号", "避免只在兴趣驱动下行动"},
        new String[]{"工程/维修", "安全与应急", "测试/运维", "技术支持"}));

    m.put("ISFP", p("ISFP", "温柔的体验家",
        "看重当下体验与内心价值，做事更偏向自然流动与审美，喜欢低压力环境。",
        new String[]{"审美与感受力强", "友善温和，包容度高", "对细节体验敏感"},
        new String[]{"给自己设定轻量计划，避免拖延", "在重要事项上更主动表达", "面对冲突先说事实再说感受"},
        new String[]{"设计/视觉", "新媒体/内容", "服务行业", "手工与创作"}));

    m.put("INFP", p("INFP", "理想的探索者",
        "重视价值与真诚，愿意为喜欢的事情投入热情，喜欢自由、有意义的工作方式。",
        new String[]{"同理心强，尊重个体差异", "创意丰富，表达真诚", "能坚持内在价值"},
        new String[]{"把“喜欢”变成“行动清单”", "减少完美主义带来的拖延", "给自己设定边界，避免过度迎合"},
        new String[]{"写作/编辑", "心理与教育", "品牌/策划", "公益与社区"}));

    m.put("INTP", p("INTP", "好奇的思考者",
        "喜欢钻研原理与模型，追求解释的准确性，擅长在复杂信息中找到规律。",
        new String[]{"学习能力强，概念理解快", "善于抽象与建模", "独立思考，不盲从"},
        new String[]{"把想法落到可交付成果上", "练习用“结论先行”沟通", "注意作息与情绪管理"},
        new String[]{"研发/算法", "数据科学", "系统分析", "科研/教育"}));

    m.put("ESTP", p("ESTP", "行动的推动者",
        "喜欢直接上手与快速反馈，敢试敢闯，擅长在动态环境中抓机会、促成交。",
        new String[]{"行动力强，敢决策", "社交灵活，反应快", "擅长谈判与推进"},
        new String[]{"重要决策前补齐数据与风险评估", "避免只追求刺激忽略长期", "把经验沉淀成可复用流程"},
        new String[]{"销售/商务", "市场推广", "运营管理", "应急与现场管理"}));

    m.put("ESFP", p("ESFP", "热情的参与者",
        "乐于互动与表达，擅长营造氛围，偏好真实体验与即时反馈，感染力强。",
        new String[]{"亲和力强，带动团队氛围", "执行快，善于应变", "注重体验与关系"},
        new String[]{"把精力分配到最关键的事上", "练习延迟满足与长期规划", "在高压下用清单稳住节奏"},
        new String[]{"活动/主持", "客户成功", "培训与服务", "内容与社群运营"}));

    m.put("ENFP", p("ENFP", "灵感的连接者",
        "对人和可能性敏感，擅长把点子串成机会，热情外向，喜欢有成长空间的环境。",
        new String[]{"创意多，善于激发他人", "沟通表达强，适应变化", "价值驱动，学习热情高"},
        new String[]{"用里程碑管理项目，减少半途分心", "重要决定先验证再扩大投入", "注意精力管理与收尾能力"},
        new String[]{"产品/运营", "市场与品牌", "教育培训", "创业/创新岗位"}));

    m.put("ENTP", p("ENTP", "机智的辩手",
        "喜欢挑战假设与探索新方案，擅长辩证思考和快速迭代，能把问题变成机会。",
        new String[]{"思维敏捷，点子多", "善于辩证与创新", "抗挫折，愿意试错"},
        new String[]{"把观点落在可执行实验上", "注意倾听与尊重他人节奏", "避免为了反驳而反驳"},
        new String[]{"创新咨询", "产品策划", "创业", "研发/解决方案"}));

    m.put("ESTJ", p("ESTJ", "高效的组织者",
        "重视效率与规则，擅长组织资源、分配任务、推进执行，让团队按目标前进。",
        new String[]{"组织与管理能力强", "结果导向，执行到位", "敢负责，善于决断"},
        new String[]{"在管理中加入更多倾听与授权", "避免用同一标准要求所有人", "关注团队氛围与反馈"},
        new String[]{"团队管理", "项目管理", "运营与行政", "供应链与生产"}));

    m.put("ESFJ", p("ESFJ", "关系的协调者",
        "在意团队氛围与共识，擅长照顾细节、协调关系，让大家更愿意一起合作。",
        new String[]{"沟通协调强", "责任感强，细致可靠", "擅长服务与支持"},
        new String[]{"建立边界，避免过度承担", "在冲突中坚持原则与事实", "给自己留出独处恢复"},
        new String[]{"人力资源", "客户成功/服务", "行政与运营", "教育与社群管理"}));

    m.put("ENFJ", p("ENFJ", "激励的领导者",
        "善于洞察团队需求并鼓舞他人，重视共同目标与成长，擅长组织协作与带动氛围。",
        new String[]{"领导与影响力强", "共情与沟通能力强", "擅长培养与推动团队"},
        new String[]{"目标要量化，避免只靠热情", "注意过度投入带来的疲惫", "用数据支持判断"},
        new String[]{"团队管理", "培训讲师", "公共关系", "产品/项目管理"}));

    m.put("ENTJ", p("ENTJ", "果断的指挥者",
        "目标清晰、推进迅速，擅长做决策与资源整合，喜欢用结构化方式解决大问题。",
        new String[]{"战略与决策能力强", "执行与推动力强", "擅长系统化管理"},
        new String[]{"更多倾听一线信息与反馈", "在效率之外兼顾人的感受", "为团队预留试错空间"},
        new String[]{"管理者/负责人", "战略运营", "咨询", "项目与产品管理"}));

    PROFILES = Collections.unmodifiableMap(m);
  }

  /**
   * 从结果文本中提取 MBTI 类型（例如："ENFP"）。
   * 兼容 resultText 中带有中文、空格等情况。
   */
  public static String extractType(String resultText) {
    if (resultText == null) {
      return null;
    }
    Matcher matcher = TYPE_PATTERN.matcher(resultText);
    if (!matcher.find()) {
      return null;
    }
    return matcher.group(1).toUpperCase();
  }

  public static Profile getProfile(String type) {
    if (type == null) {
      return null;
    }
    return PROFILES.get(type.toUpperCase());
  }

  public static Profile fromResultText(String resultText) {
    String type = extractType(resultText);
    return getProfile(type);
  }
}
