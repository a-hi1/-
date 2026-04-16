SET FOREIGN_KEY_CHECKS=0;
SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for assessments
-- ----------------------------
DROP TABLE IF EXISTS `assessments`;
CREATE TABLE `assessments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(20) NOT NULL,
  `cost` double DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of assessments
-- ----------------------------
INSERT INTO `assessments` VALUES ('3', 'mbti职业测试16题版', '50', '1');
INSERT INTO `assessments` VALUES ('4', 'mbti职业性格测试20题版', '100', '1');

-- ----------------------------
-- Table structure for choices
-- ----------------------------
DROP TABLE IF EXISTS `choices`;
CREATE TABLE `choices` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question_id` int NOT NULL COMMENT '考题ID',
  `title` varchar(255) DEFAULT NULL COMMENT '选项',
  `checked` tinyint DEFAULT NULL COMMENT '是否是正确答案',
  `hint` varchar(255) DEFAULT NULL COMMENT '提示',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of choices
-- ----------------------------
INSERT INTO `choices` VALUES ('13', '5', '计划你要做什么或在什么时候做', '1', null);
INSERT INTO `choices` VALUES ('14', '5', '说去就去', '0', null);
INSERT INTO `choices` VALUES ('17', '6', '较为随兴所至的人', '0', null);
INSERT INTO `choices` VALUES ('18', '6', '较为有条理的人', '1', null);
INSERT INTO `choices` VALUES ('21', '7', '凭兴所致行事', '0', null);
INSERT INTO `choices` VALUES ('22', '7', '按照计划行事', '1', null);
INSERT INTO `choices` VALUES ('25', '8', '合你心意', '1', null);
INSERT INTO `choices` VALUES ('26', '8', '令你感到束缚', '0', null);
INSERT INTO `choices` VALUES ('29', '9', '开始前小心组织计划', '1', null);
INSERT INTO `choices` VALUES ('30', '9', '边做边找需做什么', '0', null);
INSERT INTO `choices` VALUES ('33', '10', '你的情感支配你的理智', '0', null);
INSERT INTO `choices` VALUES ('34', '10', '你的理智主宰你的情感', '1', null);
INSERT INTO `choices` VALUES ('37', '11', '重视感情多于逻辑', '0', null);
INSERT INTO `choices` VALUES ('38', '11', '重视逻辑多于感情', '1', null);
INSERT INTO `choices` VALUES ('41', '12', '温柔', '0', null);
INSERT INTO `choices` VALUES ('42', '12', '坚定', '1', null);
INSERT INTO `choices` VALUES ('45', '13', '思考', '1', null);
INSERT INTO `choices` VALUES ('46', '13', '感受', '0', null);
INSERT INTO `choices` VALUES ('49', '14', '敏感', '0', null);
INSERT INTO `choices` VALUES ('50', '14', '公正', '1', null);
INSERT INTO `choices` VALUES ('53', '15', '以事实为主的课程', '1', null);
INSERT INTO `choices` VALUES ('54', '15', '涉及理论的课程', '0', null);
INSERT INTO `choices` VALUES ('57', '16', '富于想象力的的人', '0', null);
INSERT INTO `choices` VALUES ('58', '16', '现实的人', '1', null);
INSERT INTO `choices` VALUES ('61', '17', '实事求是的人', '1', null);
INSERT INTO `choices` VALUES ('62', '17', '机灵的人', '0', null);
INSERT INTO `choices` VALUES ('65', '18', '常提出新主意的', '0', null);
INSERT INTO `choices` VALUES ('66', '18', '脚踏实地的', '1', null);
INSERT INTO `choices` VALUES ('69', '19', '一位思维敏捷及非常聪颖的人', '0', null);
INSERT INTO `choices` VALUES ('70', '19', '实事求是，具有丰富常识的人', '1', null);
INSERT INTO `choices` VALUES ('73', '20', '与人容易混熟', '1', null);
INSERT INTO `choices` VALUES ('74', '20', '比较沉静或矜持', '0', null);
INSERT INTO `choices` VALUES ('77', '21', '容易让人了解', '1', null);
INSERT INTO `choices` VALUES ('78', '21', '难于让人了解', '0', null);
INSERT INTO `choices` VALUES ('81', '22', '重视自我隐私的人', '0', null);
INSERT INTO `choices` VALUES ('82', '22', '非常坦率开放的人', '1', null);
INSERT INTO `choices` VALUES ('85', '23', '一个人独处', '0', null);
INSERT INTO `choices` VALUES ('86', '23', '和别人在一起', '1', null);
INSERT INTO `choices` VALUES ('89', '24', '令你活力倍增', '1', null);
INSERT INTO `choices` VALUES ('90', '24', '常常令你心里憔悴', '0', null);
 

-- ----------------------------
-- Table structure for class_teams
-- ----------------------------
DROP TABLE IF EXISTS `class_teams`;
CREATE TABLE `class_teams` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次名称',
  `begin_year` date DEFAULT NULL COMMENT '开始时间',
  `status` int DEFAULT NULL COMMENT '状态',
  `creator_id` int DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of class_teams
-- ----------------------------
INSERT INTO `class_teams` VALUES ('2', '2021年10月第一批', '2021-10-01', '2', '1');

-- ----------------------------
-- Table structure for exams
-- ----------------------------
DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams` (
  `id` int NOT NULL AUTO_INCREMENT,
  `personnel_id` int DEFAULT NULL,
  `schedule_id` int DEFAULT NULL,
  `begin_time` date DEFAULT NULL,
  `end_time` date DEFAULT NULL,
  `result` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of exams
-- ----------------------------

-- ----------------------------
-- Table structure for exam_questions
-- ----------------------------
DROP TABLE IF EXISTS `exam_questions`;
CREATE TABLE `exam_questions` (
  `exam_id` int NOT NULL DEFAULT '0',
  `personnel_id` int NOT NULL DEFAULT '0',
  `question_id` int NOT NULL DEFAULT '0',
  `answer` varchar(60) DEFAULT NULL,
  `result` tinyint DEFAULT NULL,
  `score` int DEFAULT NULL,
  PRIMARY KEY (`exam_id`,`personnel_id`,`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of exam_questions
-- ----------------------------

-- ----------------------------
-- Table structure for paper_questions
-- ----------------------------
DROP TABLE IF EXISTS `paper_questions`;
CREATE TABLE `paper_questions` (
  `paper_id` int NOT NULL DEFAULT '0',
  `question_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`paper_id`,`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of paper_questions
-- ----------------------------

-- ----------------------------
-- Table structure for personality_dimension
-- ----------------------------
DROP TABLE IF EXISTS `personality_dimension`;
CREATE TABLE `personality_dimension` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `depict` varchar(255) DEFAULT NULL,
  `assessment_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of personality_dimension
-- ----------------------------
INSERT INTO `personality_dimension` VALUES ('1', '外倾型（E）-内倾型（I）', '外倾的人倾向于将注意力和精力投注在外部世界，外在的人，外在的物，外在的环境等，而内倾的人则相反，较为关注自我的内部状况，如内心情感、思想。', '1');
INSERT INTO `personality_dimension` VALUES ('2', '感觉型（S）—直觉型（N）', '感觉型的人信赖五官听到、看到、闻到、感觉到、尝到的实实在在、有形有据的事实和信息，而直觉型的人注重“第六感觉”，注重“弦外之音”，直觉型的人的许多结论在感觉型的人眼里，也许是飘忽的，不实在的。', '1');
INSERT INTO `personality_dimension` VALUES ('3', '思维型（T）—情感型（F）', '情感型的人常从自我的价值观念出发，做出一些自己认定是对的决策，比较关注决策可能给他人带来的情绪体验，人情味较浓。思维型的人则比较注重依据客观事实的分析，不太习惯根据人情因素变通，哪怕做出的决定并不令人舒服。', '1');
INSERT INTO `personality_dimension` VALUES ('4', '判断型（J）—知觉型（P）', '知觉型的人会不断关注新的信息，喜欢变化，也会考虑许多可能的变化因素，更愿意以比较灵活、随意、开放的方式生活。在做决策时，判断型的人较为果断，而知觉型的人总希望获得更多信息后再决断。逛了两天商场，还决定不了买什么的人，多半是知觉型的。', '1');
INSERT INTO `personality_dimension` VALUES ('5', '外倾型（E）-内倾型（I）', '外倾的人倾向于将注意力和精力投注在外部世界，外在的人，外在的物，外在的环境等，而内倾的人则相反，较为关注自我的内部状况，如内心情感、思想。', '3');
INSERT INTO `personality_dimension` VALUES ('6', '感觉型（S）—直觉型（N）', '感觉型的人信赖五官听到、看到、闻到、感觉到、尝到的实实在在、有形有据的事实和信息，而直觉型的人注重“第六感觉”，注重“弦外之音”，直觉型的人的许多结论在感觉型的人眼里，也许是飘忽的，不实在的。', '3');
INSERT INTO `personality_dimension` VALUES ('7', '思维型（T）—情感型（F）', '情感型的人常从自我的价值观念出发，做出一些自己认定是对的决策，比较关注决策可能给他人带来的情绪体验，人情味较浓。思维型的人则比较注重依据客观事实的分析，不太习惯根据人情因素变通，哪怕做出的决定并不令人舒服。', '3');
INSERT INTO `personality_dimension` VALUES ('8', '判断型（J）—知觉型（P）', '知觉型的人会不断关注新的信息，喜欢变化，也会考虑许多可能的变化因素，更愿意以比较灵活、随意、开放的方式生活。在做决策时，判断型的人较为果断，而知觉型的人总希望获得更多信息后再决断。逛了两天商场，还决定不了买什么的人，多半是知觉型的。', '3');
INSERT INTO `personality_dimension` VALUES ('9', '外倾型（E）-内倾型（I）', '外倾的人倾向于将注意力和精力投注在外部世界，外在的人，外在的物，外在的环境等，而内倾的人则相反，较为关注自我的内部状况，如内心情感、思想。', '4');
INSERT INTO `personality_dimension` VALUES ('10', '感觉型（S）—直觉型（N）', '感觉型的人信赖五官听到、看到、闻到、感觉到、尝到的实实在在、有形有据的事实和信息，而直觉型的人注重“第六感觉”，注重“弦外之音”，直觉型的人的许多结论在感觉型的人眼里，也许是飘忽的，不实在的。', '4');
INSERT INTO `personality_dimension` VALUES ('11', '思维型（T）—情感型（F）', '情感型的人常从自我的价值观念出发，做出一些自己认定是对的决策，比较关注决策可能给他人带来的情绪体验，人情味较浓。思维型的人则比较注重依据客观事实的分析，不太习惯根据人情因素变通，哪怕做出的决定并不令人舒服。', '4');
INSERT INTO `personality_dimension` VALUES ('12', '判断型（J）—知觉型（P）', '知觉型的人会不断关注新的信息，喜欢变化，也会考虑许多可能的变化因素，更愿意以比较灵活、随意、开放的方式生活。在做决策时，判断型的人较为果断，而知觉型的人总希望获得更多信息后再决断。逛了两天商场，还决定不了买什么的人，多半是知觉型的。', '4');

-- ----------------------------
-- Table structure for questions
-- ----------------------------
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint DEFAULT NULL COMMENT '类型',
  `title` varchar(8000) DEFAULT NULL COMMENT '题目',
  `difficulty` tinyint DEFAULT NULL COMMENT '难度',
  `hint` varchar(255) DEFAULT NULL COMMENT '提示',
  `status` tinyint DEFAULT NULL COMMENT '状态',
  `assessment_id` int DEFAULT NULL COMMENT '科目ID',
  `user_id` int DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of questions
-- ----------------------------
INSERT INTO `questions` VALUES ('5', '1', '当你要外出一整天，你会', '1', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('6', '1', '你认为自己是一个', '1', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('7', '1', '处理许多事情上，你会喜欢', '1', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('8', '1', '按照程序表做事', '1', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('9', '1', '当你有一份特别的任务，你会喜欢', '4', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('10', '2', '你是否经常让', '2', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('11', '2', '你倾向', '2', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('12', '2', '下面哪一个词更合你的心意？', '2', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('13', '2', '下面那个词更合你的心意？', '4', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('14', '2', '下面哪个词更合你的心意', '2', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('15', '3', '假如你是一位老师你会选教', '3', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('16', '3', '一般来说你和哪些人比较合得来', '3', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('17', '3', '你宁愿被人认为是一个', '3', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('18', '3', '你会跟哪些人做朋友？', '3', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('19', '3', '哪些人会更吸引你？', '3', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('20', '4', '你通常', '4', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('21', '4', '你是否', '4', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('22', '4', '大多数人会说你是一个', '4', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('23', '4', '你喜欢花很多时间', '4', '', '2', '3', '1');
INSERT INTO `questions` VALUES ('24', '4', '与很多人一起会', '4', '', '2', '3', '1');

-- ----------------------------
-- Table structure for question_dimension
-- ----------------------------
DROP TABLE IF EXISTS `question_dimension`;
CREATE TABLE `question_dimension` (
  `question_id` int NOT NULL DEFAULT '0' COMMENT '考题ID',
  `dimension_id` int NOT NULL DEFAULT '0' COMMENT '知识点ID',
  PRIMARY KEY (`question_id`,`dimension_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of question_dimension
-- ----------------------------
INSERT INTO `question_dimension` VALUES ('5', '4');
INSERT INTO `question_dimension` VALUES ('6', '4');
INSERT INTO `question_dimension` VALUES ('7', '4');
INSERT INTO `question_dimension` VALUES ('8', '4');
INSERT INTO `question_dimension` VALUES ('9', '4');
INSERT INTO `question_dimension` VALUES ('10', '3');
INSERT INTO `question_dimension` VALUES ('11', '3');
INSERT INTO `question_dimension` VALUES ('12', '3');
INSERT INTO `question_dimension` VALUES ('13', '3');
INSERT INTO `question_dimension` VALUES ('14', '3');
INSERT INTO `question_dimension` VALUES ('15', '2');
INSERT INTO `question_dimension` VALUES ('16', '2');
INSERT INTO `question_dimension` VALUES ('17', '2');
INSERT INTO `question_dimension` VALUES ('18', '2');
INSERT INTO `question_dimension` VALUES ('19', '2');
INSERT INTO `question_dimension` VALUES ('20', '1');
INSERT INTO `question_dimension` VALUES ('21', '1');
INSERT INTO `question_dimension` VALUES ('22', '1');
INSERT INTO `question_dimension` VALUES ('23', '1');
INSERT INTO `question_dimension` VALUES ('24', '1');

-- ----------------------------
-- Table structure for schedules
-- ----------------------------
DROP TABLE IF EXISTS `schedules`;
CREATE TABLE `schedules` (
  `id` int NOT NULL AUTO_INCREMENT,
  `begin_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `assessment_id` int DEFAULT NULL,
  `team_id` int DEFAULT NULL,
  `question_number` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `creator_id` int DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of schedules
-- ----------------------------
INSERT INTO `schedules` VALUES ('12', '2021-10-12 10:30:00', '2021-10-12 12:30:00', '120', '3', '2', '16', '2', '1', '2021-10-05');

-- ----------------------------
-- Table structure for testpersonnel
-- ----------------------------
DROP TABLE IF EXISTS `testpersonnel`;
CREATE TABLE `testpersonnel` (
  `id` int NOT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `gender` varchar(6) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `team_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of testpersonnel
-- ----------------------------
INSERT INTO `testpersonnel` VALUES ('81', '13928282298', 'M', '2000-06-03', null, '2');
INSERT INTO `testpersonnel` VALUES ('82', '13902884229', 'M', '2002-02-06', null, '2');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(20) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `passwd` varchar(128) DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'admin', 'user admin', '123456', '1', '1', '2021-11-04 10:07:16');
INSERT INTO `users` VALUES ('2', 'operator', 'question operator', 'operator', '2', '1', '2021-09-10 15:17:04');
INSERT INTO `users` VALUES ('3', 'teacher', 'teacher', 'teacher', '3', '1', '2021-10-21 14:57:08');
INSERT INTO `users` VALUES ('81', '13928282298', '张三', '123456', '4', '1', null);
INSERT INTO `users` VALUES ('82', '13902884229', '李四', '123456', '4', '1', '2021-11-04 10:20:03');
