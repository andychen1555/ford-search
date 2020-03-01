
DROP TABLE IF EXISTS `search_record`;
CREATE TABLE `search_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `user` varchar(256) NOT NULL DEFAULT '' COMMENT '用户名',
  `gas_name` varchar(256) NOT NULL DEFAULT '' COMMENT '加油站名字',
  `gas_address` varchar(256) NOT NULL DEFAULT '' COMMENT '加油站地址',
  `ford4s_name` varchar(256) NOT NULL DEFAULT '' COMMENT '福特4S店名字',
  `ford4s_address` varchar(256) NOT NULL DEFAULT '' COMMENT '福特4S店地址',
  `numbers` bigint(20) NOT NULL COMMENT '该记录查询次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_answer` (`gas_name`,`gas_address`,`ford4s_name`,`ford4s_address`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='查询记录表';

