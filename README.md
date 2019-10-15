# admin-portal base springboot 1.5.3
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
INSERT INTO `sys_codes` VALUES ('36369841-14a2-48ef-84fc-fe80a92bb925', NULL, 'SYS_PROP', 'defaultPwd', '111111', '默认密码', NULL, '0', 4, NULL, NULL, NULL);
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
INSERT INTO `sys_dept` VALUES ('a4a48cc6-f219-4022-bd16-790a669932d4', NULL, 'TEST', 'dept_1', 1, '0', '');
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
INSERT INTO `sys_module` VALUES ('529c3de5-843e-102f-8977-9431367dd2a3', '001-004-001', '0905c3a1-8341-102f-86c1-ab092cc04000', 3, '基础参数', '0', 1, '', '/framework/sysmng/codesmng.do?action=entry&codeType=SYS_PROP&viewType=form', 'icon-tasks');
INSERT INTO `sys_module` VALUES ('529d9646-843e-102f-8977-9431367dd2a3', '001-004-002', '0905c3a1-8341-102f-86c1-ab092cc04000', 3, '地区字典', '0', 2, '', '/framework/sysmng/codesmng.do?action=entry&codeType=AREA&viewType=tree', 'icon-twitter');
INSERT INTO `sys_module` VALUES ('a01b197c-f7b0-4520-b33f-93ec800c61b9', '001_6', '0345b801-8340-102f-86c1-ab092cc04000', 2, '部门管理', '0', 4, '', '/framework/sysmng/deptmng.do?action=entry', 'icon-sitemap');
INSERT INTO `sys_module` VALUES ('d6ca1fd4-5ffe-49a2-9c01-2c98f7579493', '001_7', '0345b801-8340-102f-86c1-ab092cc04000', 2, '主题演示', '0', 6, '', '/framework/theme/metronic/admin/template_content/index.html', 'icon-compass');

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
INSERT INTO `sys_permit` VALUES ('24d747b5-a02a-4573-8cd3-fd77af1b3bd7', '70dc7369-8343-102f-86c1-ab092cc04000', 'a01b197c-f7b0-4520-b33f-93ec800c61b9');
INSERT INTO `sys_permit` VALUES ('b470ced2-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '0345b801-8340-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b470f816-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '08e5dd0b-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b47100c8-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '08ef127c-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b47105f0-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '08fceb76-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b4710ccc-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '0905c3a1-8341-102f-86c1-ab092cc04000');
INSERT INTO `sys_permit` VALUES ('b4710eb1-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '529c3de5-843e-102f-8977-9431367dd2a3');
INSERT INTO `sys_permit` VALUES ('b47115a1-00b0-11e3-9e70-000c2907a166', '70dc7369-8343-102f-86c1-ab092cc04000', '529d9646-843e-102f-8977-9431367dd2a3');
INSERT INTO `sys_permit` VALUES ('ce82204d-542c-4f95-bd01-75bedb7d5bab', '70dc7369-8343-102f-86c1-ab092cc04000', 'd6ca1fd4-5ffe-49a2-9c01-2c98f7579493');

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

