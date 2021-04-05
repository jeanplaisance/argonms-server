﻿DROP TABLE IF EXISTS `mesodrops`;

CREATE TABLE `mesodrops` (
  `monsterdropid` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `monsterid` INT(11) NOT NULL DEFAULT 0,
  `mesoamount` INT(11) NOT NULL DEFAULT 0,
  `chance` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`monsterdropid`)
) ENGINE=InnoDB;

/*!40000 ALTER TABLE `mesodrops` DISABLE KEYS */;
INSERT INTO `mesodrops` (`monsterid`,`mesoamount`,`chance`) VALUES
(100100, 5, 585000),
(100101, 10, 585000),
(120100, 10, 585000),
(130100, 15, 585000),
(130101, 15, 585000),
(210100, 15, 585000),
(1110100, 35, 585000),
(1110101, 25, 585000),
(1120100, 30, 585000),
(1130100, 40, 585000),
(1140100, 40, 585000),
(1210100, 18, 585000),
(1210101, 25, 585000),
(1210102, 20, 585000),
(1210103, 30, 585000),
(2110200, 45, 585000),
(2130100, 50, 585000),
(2130103, 42, 585000),
(2220000, 45, 585000),
(2220100, 45, 585000),
(2230100, 60, 585000),
(2230101, 55, 585000),
(2230102, 55, 585000),
(2230103, 55, 585000),
(2230104, 65, 585000),
(2230105, 55, 585000),
(2230106, 55, 585000),
(2230107, 55, 585000),
(2230108, 50, 585000),
(2230109, 65, 585000),
(2230110, 55, 585000),
(2230111, 55, 585000),
(2230200, 65, 585000),
(2300100, 40, 585000),
(3000000, 65, 585000),
(3000001, 100, 585000),
(3000002, 100, 585000),
(3000003, 100, 585000),
(3000004, 100, 585000),
(3000005, 70, 585000),
(3000006, 70, 585000),
(3100101, 95, 585000),
(3100102, 75, 585000),
(3110100, 95, 585000),
(3110101, 100, 585000),
(3110102, 100, 585000),
(3210100, 95, 585000),
(3210200, 100, 585000),
(3210201, 100, 585000),
(3210202, 100, 585000),
(3210203, 95, 585000),
(3210204, 105, 585000),
(3210205, 105, 585000),
(3210206, 95, 585000),
(3210207, 105, 585000),
(3210208, 95, 585000),
(3210450, 95, 585000),
(3210800, 110, 585000),
(3220000, 75, 585000),
(3230100, 75, 585000),
(3230101, 70, 585000),
(3230102, 115, 585000),
(3230103, 120, 585000),
(3230104, 100, 585000),
(3230200, 95, 585000),
(3230300, 120, 720000),
(3230301, 120, 720000),
(3230302, 95, 585000),
(3230303, 120, 585000),
(3230304, 120, 585000),
(3230305, 120, 585000),
(3230306, 120, 585000),
(3230307, 120, 585000),
(3230308, 120, 585000),
(3230400, 120, 585000),
(3230405, 120, 585000),
(4090000, 150, 558000),
(4130100, 200, 558000),
(4130101, 210, 558000),
(4130102, 215, 558000),
(4130103, 290, 558000),
(4130104, 215, 558000),
(4230100, 120, 558000),
(4230101, 130, 558000),
(4230102, 220, 558000),
(4230103, 150, 558000),
(4230104, 220, 558000),
(4230105, 150, 558000),
(4230106, 180, 558000),
(4230107, 140, 558000),
(4230108, 165, 540000),
(4230109, 160, 558000),
(4230110, 205, 558000),
(4230111, 145, 558000),
(4230112, 175, 558000),
(4230113, 135, 558000),
(4230114, 145, 558000),
(4230115, 215, 558000),
(4230116, 135, 558000),
(4230117, 160, 558000),
(4230118, 205, 558000),
(4230119, 145, 558000),
(4230120, 175, 558000),
(4230121, 205, 558000),
(4230122, 150, 558000),
(4230123, 165, 540000),
(4230124, 150, 558000),
(4230125, 175, 558000),
(4230126, 215, 558000),
(4230200, 130, 558000),
(4230201, 135, 558000),
(4230300, 200, 558000),
(4230400, 200, 558000),
(4230500, 120, 558000),
(4230501, 160, 558000),
(4230502, 165, 540000),
(4230503, 180, 558000),
(4230504, 180, 558000),
(4230505, 220, 558000),
(4230506, 230, 558000),
(5100000, 250, 540000),
(5100002, 265, 540000),
(5100003, 250, 540000),
(5100004, 335, 540000),
(5100005, 290, 540000),
(5120000, 280, 540000),
(5120001, 290, 540000),
(5120002, 290, 540000),
(5120003, 290, 540000),
(5120100, 405, 540000),
(5120500, 335, 540000),
(5120501, 290, 540000),
(5120502, 300, 540000),
(5120503, 250, 540000),
(5120504, 260, 540000),
(5120505, 360, 540000),
(5120506, 315, 540000),
(5130100, 250, 540000),
(5130101, 320, 540000),
(5130102, 350, 540000),
(5130103, 280, 540000),
(5130104, 320, 540000),
(5130105, 335, 540000),
(5130107, 340, 450000),
(5130108, 340, 540000),
(5140000, 350, 540000),
(5150000, 370, 540000),
(5150001, 335, 540000),
(5200000, 75, 540000),
(5200001, 80, 540000),
(5200002, 80, 540000),
(5220000, 300, 585000),
(5300000, 85, 540000),
(5300001, 90, 540000),
(5300100, 300, 540000),
(5400000, 95, 540000),
(6090000, 380, 540000),
(6130100, 380, 540000),
(6130101, 1000, 900000),
(6130102, 380, 540000),
(6130103, 380, 540000),
(6130200, 390, 540000),
(6130202, 380, 540000),
(6130203, 380, 540000),
(6130204, 405, 540000),
(6130207, 400, 540000),
(6130208, 415, 540000),
(6130209, 410, 540000),
(6230100, 400, 540000),
(6230200, 405, 540000),
(6230201, 405, 540000),
(6230300, 400, 540000),
(6230400, 410, 540000),
(6230500, 415, 540000),
(6230600, 405, 540000),
(6230601, 415, 540000),
(6230602, 410, 540000),
(6300000, 410, 540000),
(6300001, 410, 540000),
(6300002, 410, 540000),
(6300005, 1200, 900000),
(6300100, 450, 540000),
(6400000, 415, 540000),
(6400001, 415, 540000),
(6400002, 415, 540000),
(6400100, 480, 540000),
(7130000, 440, 540000),
(7130001, 430, 540000),
(7130002, 430, 540000),
(7130003, 450, 540000),
(7130004, 455, 540000),
(7130010, 455, 540000),
(7130020, 455, 540000),
(7130100, 420, 540000),
(7130101, 480, 540000),
(7130103, 440, 540000),
(7130104, 420, 540000),
(7130200, 460, 540000),
(7130300, 510, 540000),
(7130400, 420, 630000),
(7130401, 420, 630000),
(7130402, 420, 630000),
(7130500, 420, 540000),
(7130501, 440, 540000),
(7130600, 430, 540000),
(7130601, 450, 540000),
(7140000, 450, 540000),
(7160000, 500, 540000),
(7220000, 425, 585000),
(8130100, 2000, 720000),
(8130100, 1500, 720000),
(8140000, 530, 630000),
(8140001, 470, 540000),
(8140002, 475, 540000),
(8140101, 495, 540000),
(8140102, 495, 540000),
(8140103, 495, 540000),
(8140110, 485, 540000),
(8140111, 500, 540000),
(8140200, 420, 630000),
(8140300, 480, 540000),
(8140500, 760, 630000),
(8140600, 770, 630000),
(8140700, 760, 630000),
(8140701, 780, 630000),
(8140702, 790, 630000),
(8140703, 800, 630000),
(8141000, 780, 630000),
(8141100, 800, 630000),
(8141300, 785, 630000),
(8142000, 790, 630000),
(8142100, 795, 630000),
(8143000, 800, 630000),
(8150100, 810, 630000),
(8150101, 815, 630000),
(8150200, 810, 630000),
(8150201, 820, 630000),
(8150300, 800, 630000),
(8150301, 810, 630000),
(8150302, 815, 630000),
(8160000, 830, 630000),
(8170000, 830, 630000),
(8180000, 1000, 540000),
(8180000, 1500, 540000),
(8180000, 2000, 540000),
(8180001, 1000, 540000),
(8180001, 1500, 540000),
(8180001, 2000, 540000),
(8190000, 820, 630000),
(8190002, 950, 630000),
(8190003, 835, 630000),
(8190004, 840, 630000),
(8190005, 830, 630000),
(8220000, 450, 585000),
(8220001, 760, 585000),
(8500002, 10000, 450000),
(8500002, 15000, 450000),
(8500002, 20000, 450000),
(8500002, 10000, 450000),
(8500002, 15000, 450000),
(8500002, 20000, 450000),
(8500002, 10000, 450000),
(8500002, 15000, 450000),
(8500002, 20000, 450000),
(8500002, 10000, 450000),
(8500002, 15000, 450000),
(8500002, 20000, 450000),
(8510000, 10000, 450000),
(8510000, 15000, 450000),
(8510000, 20000, 450000),
(8510000, 10000, 450000),
(8510000, 15000, 450000),
(8510000, 20000, 450000),
(8510000, 10000, 450000),
(8510000, 15000, 450000),
(8510000, 20000, 450000),
(8510000, 10000, 450000),
(8510000, 15000, 450000),
(8510000, 20000, 450000),
(8520000, 10000, 450000),
(8520000, 15000, 450000),
(8520000, 20000, 450000),
(8520000, 10000, 450000),
(8520000, 15000, 450000),
(8520000, 20000, 450000),
(8520000, 10000, 450000),
(8520000, 15000, 450000),
(8520000, 20000, 450000),
(8520000, 10000, 450000),
(8520000, 15000, 450000),
(8520000, 20000, 450000),
(8800002, 10000, 450000),
(8800002, 15000, 450000),
(8800002, 20000, 450000),
(8800002, 25000, 450000),
(8800002, 10000, 450000),
(8800002, 15000, 450000),
(8800002, 20000, 450000),
(8800002, 25000, 450000),
(8800002, 10000, 450000),
(8800002, 15000, 450000),
(8800002, 20000, 450000),
(8800002, 25000, 450000),
(8800002, 10000, 450000),
(8800002, 15000, 450000),
(8800002, 20000, 450000),
(8800002, 25000, 450000),
(8810018, 10000, 450000),
(8810018, 10000, 450000),
(8810018, 10000, 450000),
(8810018, 15000, 450000),
(8810018, 15000, 450000),
(8810018, 15000, 450000),
(8810018, 20000, 450000),
(8810018, 20000, 450000),
(8810018, 20000, 450000),
(8810018, 25000, 450000),
(8810018, 25000, 450000),
(8810018, 25000, 450000),
(8810018, 30000, 450000),
(8810018, 30000, 450000),
(8810018, 30000, 450000),
(8810018, 35000, 450000),
(8810018, 35000, 450000),
(8810018, 35000, 450000),
(8810018, 90000, 166666),
(8810018, 90000, 166666),
(8810018, 105000, 166666),
(8810018, 105000, 166666),
(8810018, 105000, 166666),
(8810018, 120000, 166666),
(8810018, 120000, 166666),
(8810018, 120000, 166666);