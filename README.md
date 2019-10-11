# admin-portal
## base-db init script

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_codes
-- ----------------------------
DROP TABLE IF EXISTS `sys_codes`;
CREATE TABLE `sys_codes`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `up_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_key` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_value` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_value_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_state` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_order` int(11) NULL DEFAULT NULL,
  `code_desc` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `maintain_type` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remain_field` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_codes
-- ----------------------------
INSERT INTO `sys_codes` VALUES ('22773314-89c9-43cc-8f46-3e5b4b9946a7', NULL, 'SYS_PROP', 'sysVersion', 'V1.0', '系统版本', NULL, '0', 2, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('258c5616-7e4c-4d0a-8bd8-a81338c17524', NULL, 'AREA', '028', 'cd', '成都', NULL, '0', 1, '', NULL, '');
INSERT INTO `sys_codes` VALUES ('32b24525-0950-4420-a50d-fabd704c823d', '258c5616-7e4c-4d0a-8bd8-a81338c17524', 'AREA', '028-001', 'cd_gx', '高新区', NULL, '0', 1, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('36369841-14a2-48ef-84fc-fe80a92bb925', NULL, 'SYS_PROP', 'defaultPwd', '11111', '默认密码', NULL, '0', 4, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('7974fff6-831a-102f-86c1-ab092cc04000', NULL, 'SYS_PROP', 'sysName', 'ADMIN-控制台', '系统名称', '0', '0', 1, NULL, '', NULL);
INSERT INTO `sys_codes` VALUES ('9f99ea70-657d-4cba-81e6-41fe46f921b0', NULL, 'AREA', '010', 'bj', '北京', NULL, '0', 3, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('a130c72c-b1aa-4cd1-be05-41178236efc5', NULL, 'SYS_PROP', 'indexPageUrl', 'http://ip:port', '首页地址', NULL, '0', 5, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('a90f8262-5ebb-4729-9f35-ae41ef64d21d', '258c5616-7e4c-4d0a-8bd8-a81338c17524', 'AREA', '028_2', 'cd_qy', '青羊区', NULL, '0', 2, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('ba1ef2b5-1d7d-4caf-b8a0-d04fafff2868', NULL, 'AREA', '021', 'sh', '上海', NULL, '0', 2, '', NULL, '');
INSERT INTO `sys_codes` VALUES ('bb87ad2e-13f9-47dc-a714-ccf119930cd5', 'ba1ef2b5-1d7d-4caf-b8a0-d04fafff2868', 'AREA', '021_1', 'sh_xh', '徐汇区', NULL, '0', 1, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('bc544650-3a37-424a-bd16-1381d0847959', '9f99ea70-657d-4cba-81e6-41fe46f921b0', 'AREA', '010_1', 'bj_cy', '朝阳区', NULL, '0', 1, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('c2f99808-997a-4f41-bf96-287812d30d3f', NULL, 'SYS_PROP', 'orgName', 'X公司', '组织名称', NULL, '0', 3, NULL, NULL, NULL);
INSERT INTO `sys_codes` VALUES ('dcca67e8-02fc-48f0-a282-9bc6512fabfc', '9f99ea70-657d-4cba-81e6-41fe46f921b0', 'AREA', '010_2', 'bj_hd', '海淀区', NULL, '0', 2, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `up_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_order` int(11) NULL DEFAULT NULL,
  `dept_state` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_desc` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('63cf7c07-c54f-48df-81d0-43431482835a', 'd2d23f7e-5a35-49be-b26d-ea7d22ef1391', '新部门-1', 'dept_1_1_1', 1, '0', NULL);
INSERT INTO `sys_dept` VALUES ('8dbec82b-e924-4a31-b434-44ca086add51', 'd516289e-515e-4d91-9a1c-96c651467e11', '新部门-2', 'dept_2_1', 1, '0', NULL);
INSERT INTO `sys_dept` VALUES ('a4a48cc6-f219-4022-bd16-790a669932d4', NULL, 'TPC', 'dept_1', 1, '0', '');
INSERT INTO `sys_dept` VALUES ('d2d23f7e-5a35-49be-b26d-ea7d22ef1391', 'a4a48cc6-f219-4022-bd16-790a669932d4', '新部门-3', 'dept_1_1', 1, '0', NULL);
INSERT INTO `sys_dept` VALUES ('d516289e-515e-4d91-9a1c-96c651467e11', NULL, '新部门-4', 'dept_2', 2, '0', NULL);

-- ----------------------------
-- Table structure for sys_login
-- ----------------------------
DROP TABLE IF EXISTS `sys_login`;
CREATE TABLE `sys_login`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `login_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `login_pwd` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `login_state` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `maintain_type` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `login_order` int(11) NULL DEFAULT NULL,
  `phone_num` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mail_addr` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dept_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sys_login_inx_1`(`login_name`) USING BTREE,
  INDEX `fk_sys_login_1`(`dept_id`) USING BTREE,
  CONSTRAINT `FKahi3sbdjaikrhd648w2rmn42k` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_login_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_login
-- ----------------------------
INSERT INTO `sys_login` VALUES ('49aff965-2532-4236-9ee1-86c9501f6be7', 'data', '查询人', '8d777f385d3dfec8815d20f7496026dc', '0', NULL, 2, '', '', 'a4a48cc6-f219-4022-bd16-790a669932d4');
INSERT INTO `sys_login` VALUES ('c887bb22-80ed-102f-a5e0-289b92097530', 'admin', '系统管理员', '0192023a7bbd73250516f069df18b500', '0', '0', 1, '12345678901', 'admin@xxx.com', 'a4a48cc6-f219-4022-bd16-790a669932d4');

-- ----------------------------
-- Table structure for sys_login_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_role`;
CREATE TABLE `sys_login_role`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `login_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sys_login_role_1`(`login_id`) USING BTREE,
  INDEX `fk_sys_login_role_2`(`role_id`) USING BTREE,
  CONSTRAINT `FK9ogjr2mccccb9k22fpdt5bbb9` FOREIGN KEY (`login_id`) REFERENCES `sys_login` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKdw0vylnvwolhcc5dai9rctxma` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_login_role_ibfk_1` FOREIGN KEY (`login_id`) REFERENCES `sys_login` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_login_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_login_role
-- ----------------------------
INSERT INTO `sys_login_role` VALUES ('4e68fd94-f073-4da3-9129-9d7d8411b871', '49aff965-2532-4236-9ee1-86c9501f6be7', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9');
INSERT INTO `sys_login_role` VALUES ('97886d53-abef-4f31-a6ca-38d409a41356', 'c887bb22-80ed-102f-a5e0-289b92097530', '70dc7369-8343-102f-86c1-ab092cc04000');

-- ----------------------------
-- Table structure for sys_module
-- ----------------------------
DROP TABLE IF EXISTS `sys_module`;
CREATE TABLE `sys_module`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `module_no` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `up_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `node_level` int(11) NULL DEFAULT NULL,
  `module_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `module_state` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `module_order` int(11) NULL DEFAULT NULL,
  `module_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `url` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sys_module_inx_1`(`module_no`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_module
-- ----------------------------
INSERT INTO `sys_module` VALUES ('0345b801-8340-102f-86c1-ab092cc04000', '001', NULL, 1, '系统管理', '0', 1, '', '', 'icon-wrench');
INSERT INTO `sys_module` VALUES ('08e5dd0b-8341-102f-86c1-ab092cc04000', '001-001', '0345b801-8340-102f-86c1-ab092cc04000', 2, '功能管理', '0', 1, '进行功能菜单和权限配置', '/framework/sysmng/modulemng.do?action=entry', 'icon-dollar');
INSERT INTO `sys_module` VALUES ('08ef127c-8341-102f-86c1-ab092cc04000', '001-002', '0345b801-8340-102f-86c1-ab092cc04000', 2, '用户管理', '0', 2, '配置用户和部门', '/framework/sysmng/usermng.do?action=entry', 'icon-yen');
INSERT INTO `sys_module` VALUES ('08fceb76-8341-102f-86c1-ab092cc04000', '001-003', '0345b801-8340-102f-86c1-ab092cc04000', 2, '角色管理', '0', 3, '', '/framework/sysmng/rolemng.do?action=entry', 'icon-truck');
INSERT INTO `sys_module` VALUES ('0905c3a1-8341-102f-86c1-ab092cc04000', '001-004', '0345b801-8340-102f-86c1-ab092cc04000', 2, '系统设置', '0', 5, '', '', 'icon-sun');
INSERT INTO `sys_module` VALUES ('117c3535-4873-4ed6-8dcd-85852b6cad99', 'm_2_13_7', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, '车辆数据转换', '0', 7, '车辆信号、车况的数据转换', '/iov/metadata/vehDataConvert/entry', 'icon-tasks');
INSERT INTO `sys_module` VALUES ('121b37b3-6922-4063-a8ef-31de1b598012', 'm_2_13_4', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, '车系DTC', '0', 4, '配置车系对于DTC数据', '/iov/metadata/vehDtc/entry', 'icon-won');
INSERT INTO `sys_module` VALUES ('1eecfa56-b47f-4429-b449-d8e6405e2e0d', 'm_2_10_4', '84e51cde-68da-42b2-9b93-2b881f6d7e85', 3, '分发配置', '0', 4, '', '/iov/metadata/mpdEventTopic/entry', 'icon-sort-by-attributes-alt');
INSERT INTO `sys_module` VALUES ('3a451526-02e1-4fec-a16e-dca47e3d8595', 'm_2_13', '5a429f2b-7555-46ca-ab81-ba08b86db3eb', 2, '车辆数据', '0', 3, '', '', 'icon-sitemap');
INSERT INTO `sys_module` VALUES ('411fcf2b-e6f7-4542-b76f-2eb71f3c013b', 'm_2_15_5', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, '错误码字典', '0', 4, '联车平台统一的错误码定义(待采用)', '/framework/sysmng/codesmng.do?action=entry&codeType=ERROR_CODE&viewType=grid', 'icon-thumbs-down');
INSERT INTO `sys_module` VALUES ('41fbad73-8014-41cf-a45c-2349e5f704b9', 'm_2_15_7', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, '定时任务字典', '0', 6, '设置Iov-task服务中各定时任务的开关', ' /framework/sysmng/codesmng.do?action=entry&codeType=IOV_CONSOLE_TASK&viewType=grid', 'icon-compass');
INSERT INTO `sys_module` VALUES ('4e9444d6-85c8-403e-af4f-4204cfdbcfa2', 'm_2_13_2', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, '车系信号配置', '0', 5, '', '/iov/metadata/vehDidSignal/entry', 'icon-certificate');
INSERT INTO `sys_module` VALUES ('529c3de5-843e-102f-8977-9431367dd2a3', '001-004-001', '0905c3a1-8341-102f-86c1-ab092cc04000', 3, '基础参数', '0', 1, '', '/framework/sysmng/codesmng.do?action=entry&codeType=SYS_PROP&viewType=form', 'icon-tasks');
INSERT INTO `sys_module` VALUES ('529d9646-843e-102f-8977-9431367dd2a3', '001-004-002', '0905c3a1-8341-102f-86c1-ab092cc04000', 3, '地区字典', '0', 2, '', '/framework/sysmng/codesmng.do?action=entry&codeType=AREA&viewType=tree', 'icon-twitter');
INSERT INTO `sys_module` VALUES ('5301b0f8-dd1f-4113-a561-f1c82aaa25fa', 'm_2_10_2', '84e51cde-68da-42b2-9b93-2b881f6d7e85', 3, '车况结构', '0', 2, '车系信号-标准车况转换结构配置', '/iov/metadata/vehStatusStruc/entry', 'icon-sitemap');
INSERT INTO `sys_module` VALUES ('5339eb17-6026-43a4-98d4-a9bc4957dfb2', 'm_2_15_6', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, '标准车况别名字典', '0', 5, '设置标准车况发到外部系统的别名(和启明互通)', '/framework/sysmng/codesmng.do?action=entry&codeType=VEH_STATUS_ALIAS&viewType=grid', 'icon-star');
INSERT INTO `sys_module` VALUES ('53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 'm_2_15', '5a429f2b-7555-46ca-ab81-ba08b86db3eb', 2, '字典维护', '0', 1, '', '', 'icon-cogs');
INSERT INTO `sys_module` VALUES ('55cd80d3-4170-4d0f-9e72-5209db9b18fc', 'm_2_10_3', '84e51cde-68da-42b2-9b93-2b881f6d7e85', 3, '分发队列', '0', 3, '', '/iov/metadata/mpdTopic/entry', 'icon-compass');
INSERT INTO `sys_module` VALUES ('57119ca4-3479-4473-8caa-1867297af650', 'm_2_4_2', 'ec8c38d5-2165-4c48-bb37-4c256e2c9a25', 3, '下载地址配置', '0', 1, '下发升级包URL时,进行下载地址转换', '/framework/sysmng/codesmng.do?action=entry&codeType=VG_DOWNLOAD_URL&viewType=grid', 'icon-cogs');
INSERT INTO `sys_module` VALUES ('5a429f2b-7555-46ca-ab81-ba08b86db3eb', 'm_2', NULL, 1, '配置数据维护', '0', 2, '', '', 'icon-money');
INSERT INTO `sys_module` VALUES ('794ce668-b748-466d-9386-1acde6802d14', 'm_4_2', 'c18a809a-d981-4676-a94d-52b30c720924', 2, '车辆快照查询', '0', 2, '', '/iov/monitor/vehStatusSnapshot/entry', 'icon-signin');
INSERT INTO `sys_module` VALUES ('84e51cde-68da-42b2-9b93-2b881f6d7e85', 'm_2_10', '5a429f2b-7555-46ca-ab81-ba08b86db3eb', 2, '标准化数据', '0', 2, '', '', 'icon-signal');
INSERT INTO `sys_module` VALUES ('914bf968-d858-4e43-8df1-7e9fc2b1a640', 'm_2_13_3', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, 'ECU配置数据', '0', 1, '', '/iov/metadata/ecu/entry', 'icon-screenshot');
INSERT INTO `sys_module` VALUES ('96a885f5-3a3b-42a9-a6c5-66ed967493f6', 'm_4_1', 'c18a809a-d981-4676-a94d-52b30c720924', 2, '远控命令查询', '0', 1, '', '/iov/monitor/remoteOperation/entry', 'icon-thumbs-up');
INSERT INTO `sys_module` VALUES ('a01b197c-f7b0-4520-b33f-93ec800c61b9', '001_6', '0345b801-8340-102f-86c1-ab092cc04000', 2, '部门管理', '0', 4, '', '/framework/sysmng/deptmng.do?action=entry', 'icon-sitemap');
INSERT INTO `sys_module` VALUES ('c18a809a-d981-4676-a94d-52b30c720924', 'm_4', NULL, 1, '业务数据查询', '0', 3, '', '', 'icon-won');
INSERT INTO `sys_module` VALUES ('cbeedd63-35bb-4ae9-9981-d0ee47717a88', 'm_2_13_6', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, '车系ECU', '0', 6, '车系对应ECU清单', '/iov/metadata/vehEcu/entry', 'icon-compass');
INSERT INTO `sys_module` VALUES ('d6ca1fd4-5ffe-49a2-9c01-2c98f7579493', '001_7', '0345b801-8340-102f-86c1-ab092cc04000', 2, '主题演示', '0', 6, '', '/framework/theme/metronic/admin/template_content/index.html', 'icon-compass');
INSERT INTO `sys_module` VALUES ('d93eef43-9656-4b35-b221-558562f13881', 'm_2_15_4', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, '车辆事件字典', '0', 3, '配置车辆事件', '/iov/metadata/event/entry', 'icon-star');
INSERT INTO `sys_module` VALUES ('dbbff823-4a7b-4824-bd48-ff7a9292495a', 'm_4_3', 'c18a809a-d981-4676-a94d-52b30c720924', 2, '在线车辆查询', '0', 3, '', '/iov/monitor/vehOnline/entry', 'icon-compass');
INSERT INTO `sys_module` VALUES ('dc58ec70-d37e-4af0-92d6-e9e5863c5a46', 'm_2_10_1', '84e51cde-68da-42b2-9b93-2b881f6d7e85', 3, '标准车况', '0', 1, '定义标准车况数据', '/iov/metadata/status/entry', 'icon-eur');
INSERT INTO `sys_module` VALUES ('e12d37a4-c388-4ec5-bbec-46e776b6ce2f', 'm_2_13_1', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, '信号配置数据', '0', 2, '', '/iov/metadata/signal/entry', 'icon-dollar');
INSERT INTO `sys_module` VALUES ('e4dff7c4-29df-45be-91e6-0debdf6a06a7', 'm_2_15_3', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, '车系字典', '0', 2, '车厂的车系数据和联车平台车系数据对照关系', '/framework/sysmng/codesmng.do?action=entry&codeType=VEH_SERIES_MAPPING&viewType=grid', 'icon-eur');
INSERT INTO `sys_module` VALUES ('e8025c2b-10c3-4f62-be90-5844f38f5284', 'm_2_15_8', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, '车厂-远控字典', '0', 7, '', '/framework/sysmng/codesmng.do?action=entry&codeType=MANU_CONTROL&viewType=tree', 'icon-yen');
INSERT INTO `sys_module` VALUES ('ec8c38d5-2165-4c48-bb37-4c256e2c9a25', 'm_2_4', '5a429f2b-7555-46ca-ab81-ba08b86db3eb', 2, '网关参数配置', '0', 4, '车辆网关参数设置', '', 'icon-book');
INSERT INTO `sys_module` VALUES ('f5d91beb-ed9c-4aa8-a68d-8433143c6ae3', 'm_2_15_1', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2', 3, 'DTC故障打分字典', '0', 1, '', '/framework/sysmng/codesmng.do?action=entry&codeType=DTC_TOTAL_SCORE&viewType=grid', 'icon-cogs');
INSERT INTO `sys_module` VALUES ('faddca09-67ba-4218-8cb9-92281d893183', 'm_2_13_5', '3a451526-02e1-4fec-a16e-dca47e3d8595', 3, 'DID配置数据', '0', 3, '', '/iov/metadata/did/entry', 'icon-globe');

-- ----------------------------
-- Table structure for sys_module_function
-- ----------------------------
DROP TABLE IF EXISTS `sys_module_function`;
CREATE TABLE `sys_module_function`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `func_desc` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `module_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `func_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `func_order` int(11) NULL DEFAULT NULL,
  `url` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sys_module_function_1`(`module_id`) USING BTREE,
  CONSTRAINT `FKpfnwc36o0lg54j43l9nm2ku5k` FOREIGN KEY (`module_id`) REFERENCES `sys_module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_module_function_ibfk_1` FOREIGN KEY (`module_id`) REFERENCES `sys_module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_permit
-- ----------------------------
DROP TABLE IF EXISTS `sys_permit`;
CREATE TABLE `sys_permit`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `module_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sys_permit_1`(`role_id`) USING BTREE,
  INDEX `fk_sys_permit_2`(`module_id`) USING BTREE,
  CONSTRAINT `FK62a587hs9c3pexm3mudo0vu28` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKnb3vjgx3tcfgrekvbff8qtll9` FOREIGN KEY (`module_id`) REFERENCES `sys_module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_permit_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_permit_ibfk_2` FOREIGN KEY (`module_id`) REFERENCES `sys_module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permit
-- ----------------------------
INSERT INTO `sys_permit` VALUES ('06b6c4a2-4668-4583-bc25-8e73a5a1ef69', '70dc7369-8343-102f-86c1-ab092cc04000', '5339eb17-6026-43a4-98d4-a9bc4957dfb2');
INSERT INTO `sys_permit` VALUES ('0ef9f0e7-1cdb-4285-b849-2f631e807632', '70dc7369-8343-102f-86c1-ab092cc04000', 'cbeedd63-35bb-4ae9-9981-d0ee47717a88');
INSERT INTO `sys_permit` VALUES ('12d00fe4-0cfb-4be6-80a4-ddccc90da980', '70dc7369-8343-102f-86c1-ab092cc04000', '5a429f2b-7555-46ca-ab81-ba08b86db3eb');
INSERT INTO `sys_permit` VALUES ('1c93ae03-1b60-435b-a985-277be012d58e', '70dc7369-8343-102f-86c1-ab092cc04000', '411fcf2b-e6f7-4542-b76f-2eb71f3c013b');
INSERT INTO `sys_permit` VALUES ('1dd9be8c-69ae-4967-86b6-d612de386223', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', 'd93eef43-9656-4b35-b221-558562f13881');
INSERT INTO `sys_permit` VALUES ('21578803-f028-4004-b2d5-2fb3a85a283e', '70dc7369-8343-102f-86c1-ab092cc04000', '1eecfa56-b47f-4429-b449-d8e6405e2e0d');
INSERT INTO `sys_permit` VALUES ('24d747b5-a02a-4573-8cd3-fd77af1b3bd7', '70dc7369-8343-102f-86c1-ab092cc04000', 'a01b197c-f7b0-4520-b33f-93ec800c61b9');
INSERT INTO `sys_permit` VALUES ('26d95222-e157-4e3b-b8ab-8d9750ad7ced', '70dc7369-8343-102f-86c1-ab092cc04000', 'f5d91beb-ed9c-4aa8-a68d-8433143c6ae3');
INSERT INTO `sys_permit` VALUES ('2ff54cc6-6041-43ef-a782-51762863d969', '70dc7369-8343-102f-86c1-ab092cc04000', 'dc58ec70-d37e-4af0-92d6-e9e5863c5a46');
INSERT INTO `sys_permit` VALUES ('33ef8c3a-e751-4c9f-8a75-57251c756dcd', '70dc7369-8343-102f-86c1-ab092cc04000', '121b37b3-6922-4063-a8ef-31de1b598012');
INSERT INTO `sys_permit` VALUES ('3d92986e-5c59-4147-9956-fbd0de56276e', '70dc7369-8343-102f-86c1-ab092cc04000', '57119ca4-3479-4473-8caa-1867297af650');
INSERT INTO `sys_permit` VALUES ('4e14538f-7067-4066-b30f-704f82d83d22', '70dc7369-8343-102f-86c1-ab092cc04000', 'c18a809a-d981-4676-a94d-52b30c720924');
INSERT INTO `sys_permit` VALUES ('4fd75ffe-1ffb-4de1-9077-21b314bf6ef7', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', '84e51cde-68da-42b2-9b93-2b881f6d7e85');
INSERT INTO `sys_permit` VALUES ('548f29a4-4bf4-4b8c-9bde-c626cf0cfae4', '70dc7369-8343-102f-86c1-ab092cc04000', 'e12d37a4-c388-4ec5-bbec-46e776b6ce2f');
INSERT INTO `sys_permit` VALUES ('5ccb7f23-d63b-4d9d-8485-bd71f73ea641', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2');
INSERT INTO `sys_permit` VALUES ('74958221-46a2-4c47-a9ea-b03dd9247838', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', 'dc58ec70-d37e-4af0-92d6-e9e5863c5a46');
INSERT INTO `sys_permit` VALUES ('8078ba90-94ce-4950-9850-2e9cfbce2927', '70dc7369-8343-102f-86c1-ab092cc04000', '5301b0f8-dd1f-4113-a561-f1c82aaa25fa');
INSERT INTO `sys_permit` VALUES ('96e8a090-e105-4bbf-990a-d4e185926914', '70dc7369-8343-102f-86c1-ab092cc04000', 'ec8c38d5-2165-4c48-bb37-4c256e2c9a25');
INSERT INTO `sys_permit` VALUES ('98eab982-8adc-4adc-81c0-bfb7ec803a20', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', '5a429f2b-7555-46ca-ab81-ba08b86db3eb');
INSERT INTO `sys_permit` VALUES ('991be24e-191f-47e7-8e77-13787d67ea3a', '70dc7369-8343-102f-86c1-ab092cc04000', 'e8025c2b-10c3-4f62-be90-5844f38f5284');
INSERT INTO `sys_permit` VALUES ('9963294f-9198-4448-bb06-ce20d6922898', '70dc7369-8343-102f-86c1-ab092cc04000', 'd93eef43-9656-4b35-b221-558562f13881');
INSERT INTO `sys_permit` VALUES ('b1f08202-1d58-489d-9877-424c77bdaabb', '70dc7369-8343-102f-86c1-ab092cc04000', '96a885f5-3a3b-42a9-a6c5-66ed967493f6');
INSERT INTO `sys_permit` VALUES ('b470ced2-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '0345b801-8340-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b470f816-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '08e5dd0b-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b47100c8-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '08ef127c-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b47105f0-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '08fceb76-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b4710ccc-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '0905c3a1-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b4710eb1-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '529c3de5-843e-102f-8977-9431367dd2a3');
INSERT INTO `sys_permit` VALUES ('b47115a1-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '529d9646-843e-102f-8977-9431367dd2a3');
INSERT INTO `sys_permit` VALUES ('b85b63e3-99b4-4a01-8bc4-fc3bbdd3f76d', '70dc7369-8343-102f-86c1-ab092cc04000', '84e51cde-68da-42b2-9b93-2b881f6d7e85');
INSERT INTO `sys_permit` VALUES ('b8855d81-5eb0-48bc-8bab-cba4bdc310db', '70dc7369-8343-102f-86c1-ab092cc04000', '794ce668-b748-466d-9386-1acde6802d14');
INSERT INTO `sys_permit` VALUES ('c6d88595-9da0-4be7-8948-a54880723100', '70dc7369-8343-102f-86c1-ab092cc04000', '4e9444d6-85c8-403e-af4f-4204cfdbcfa2');
INSERT INTO `sys_permit` VALUES ('c7ee11e1-ad33-4b3a-95ca-5603aa687f21', 'aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', 'e8025c2b-10c3-4f62-be90-5844f38f5284');
INSERT INTO `sys_permit` VALUES ('c9a3b585-1dff-4d52-bb2f-dfb3040bb71f', '70dc7369-8343-102f-86c1-ab092cc04000', 'faddca09-67ba-4218-8cb9-92281d893183');
INSERT INTO `sys_permit` VALUES ('caa703bc-221f-4b1a-b0a2-099369b5c23d', '70dc7369-8343-102f-86c1-ab092cc04000', '914bf968-d858-4e43-8df1-7e9fc2b1a640');
INSERT INTO `sys_permit` VALUES ('ce82204d-542c-4f95-bd01-75bedb7d5bab', '70dc7369-8343-102f-86c1-ab092cc04000', 'd6ca1fd4-5ffe-49a2-9c01-2c98f7579493');
INSERT INTO `sys_permit` VALUES ('d2b7c993-d552-41e9-ab43-6fedb5e804d0', '70dc7369-8343-102f-86c1-ab092cc04000', '53eb2855-cfc2-4345-b6b9-5c80b0af05b2');
INSERT INTO `sys_permit` VALUES ('da12dd8b-2cd6-4254-93fa-3c6d00190e58', '70dc7369-8343-102f-86c1-ab092cc04000', 'e4dff7c4-29df-45be-91e6-0debdf6a06a7');
INSERT INTO `sys_permit` VALUES ('da911aaf-8ad8-483c-9483-3f087e34a763', '70dc7369-8343-102f-86c1-ab092cc04000', '41fbad73-8014-41cf-a45c-2349e5f704b9');
INSERT INTO `sys_permit` VALUES ('e68138c8-738a-4663-8cea-4dde8112b7dc', '70dc7369-8343-102f-86c1-ab092cc04000', '55cd80d3-4170-4d0f-9e72-5209db9b18fc');
INSERT INTO `sys_permit` VALUES ('fad92db0-bf16-47c1-b695-ff4d9eb97c7f', '70dc7369-8343-102f-86c1-ab092cc04000', '3a451526-02e1-4fec-a16e-dca47e3d8595');
INSERT INTO `sys_permit` VALUES ('fd5a95ec-0a0c-4517-bd94-37241edfb825', '70dc7369-8343-102f-86c1-ab092cc04000', '117c3535-4873-4ed6-8dcd-85852b6cad99');
INSERT INTO `sys_permit` VALUES ('ff4c7117-f312-4fc3-85d9-a10eb03119fd', '70dc7369-8343-102f-86c1-ab092cc04000', 'dbbff823-4a7b-4824-bd48-ff7a9292495a');

-- ----------------------------
-- Table structure for sys_permit_function
-- ----------------------------
DROP TABLE IF EXISTS `sys_permit_function`;
CREATE TABLE `sys_permit_function`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `func_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `permit_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_sys_permit_function_1`(`func_id`) USING BTREE,
  INDEX `fk_sys_permit_function_2`(`permit_id`) USING BTREE,
  CONSTRAINT `FKlwscxcae9yuu0syagp6i2du5t` FOREIGN KEY (`permit_id`) REFERENCES `sys_permit` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKmo13uscd96j740e67vrxyelqh` FOREIGN KEY (`func_id`) REFERENCES `sys_module_function` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_permit_function_ibfk_1` FOREIGN KEY (`func_id`) REFERENCES `sys_module_function` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_permit_function_ibfk_2` FOREIGN KEY (`permit_id`) REFERENCES `sys_permit` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role_state` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role_desc` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role_order` int(11) NULL DEFAULT NULL,
  `maintain_type` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('70dc7369-8343-102f-86c1-ab092cc04000', '系统管理角色', '0', NULL, 1, '0');
INSERT INTO `sys_role` VALUES ('aaf67837-7154-4fbc-bffe-ecbd0bf63bf9', '数据查询', '0', '', 2, NULL);

SET FOREIGN_KEY_CHECKS = 1;
