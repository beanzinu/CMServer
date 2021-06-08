-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: cmdb
-- ------------------------------------------------------
-- Server version	5.7.31-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `deposit_table`
--

DROP TABLE IF EXISTS `deposit_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposit_table` (
  `userName` varchar(45) NOT NULL,
  `deposit` int(11) DEFAULT '0',
  PRIMARY KEY (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposit_table`
--

LOCK TABLES `deposit_table` WRITE;
/*!40000 ALTER TABLE `deposit_table` DISABLE KEYS */;
INSERT INTO `deposit_table` VALUES ('min',294000),('park',195000);
/*!40000 ALTER TABLE `deposit_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_menu_table`
--

DROP TABLE IF EXISTS `group_menu_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_menu_table` (
  `group_id` int(11) NOT NULL,
  `member` varchar(45) NOT NULL,
  `menu` varchar(45) NOT NULL,
  `price` int(11) NOT NULL,
  PRIMARY KEY (`group_id`,`member`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_menu_table`
--

LOCK TABLES `group_menu_table` WRITE;
/*!40000 ALTER TABLE `group_menu_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_menu_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_table`
--

DROP TABLE IF EXISTS `group_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_table` (
  `group_id` int(11) NOT NULL,
  `group_host` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `store_name` varchar(45) CHARACTER SET latin1 NOT NULL,
  `store_category` varchar(45) CHARACTER SET latin1 NOT NULL,
  `collected_amount` int(11) NOT NULL,
  `least_price` int(11) DEFAULT '0',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_table`
--

LOCK TABLES `group_table` WRITE;
/*!40000 ALTER TABLE `group_table` DISABLE KEYS */;
INSERT INTO `group_table` VALUES (0,'g1','default','default',0,0);
/*!40000 ALTER TABLE `group_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_menu_table`
--

DROP TABLE IF EXISTS `store_menu_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_menu_table` (
  `store_name` varchar(45) NOT NULL,
  `menu` varchar(45) NOT NULL,
  `price` int(11) NOT NULL,
  PRIMARY KEY (`store_name`,`menu`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_menu_table`
--

LOCK TABLES `store_menu_table` WRITE;
/*!40000 ALTER TABLE `store_menu_table` DISABLE KEYS */;
INSERT INTO `store_menu_table` VALUES ('alchon','bulgogibab',5500),('alchon','obab',4900),('alchon','softbab',4200),('aoriramen','aoriramen',9000),('aoriramen','megaemen',10000),('aoriramen','misoramen',9000),('bonjuk','boolnakjuk',11000),('bonjuk','junbookjuk',12000),('bonjuk','shrimpjuk',9000),('hoho','jjajangmen',5900),('hoho','jjamppong',6900),('hoho','tangsuyook',12900),('hongkong','jjajanmen',5000),('hongkong','jjamppong',6000),('hongkong','tangsuyook',13000),('honydony','basakkatsu',8900),('honydony','cheesetonkatsu',11500),('honydony','hirekatsu',9900),('kingkebab','chickenkebab',5900),('kingkebab','chickenshishkebab',16900),('kingkebab','kingkebab',7900),('ligarmara','guobaro',15000),('ligarmara','marahyanguo',20000),('ligarmara','maratang',8000),('namjasusan','flatfishsashimi',27000),('namjasusan','rockfishsashimi',29000),('namjasusan','salmonsashimi',29000),('noboo','cheeseboodaeset',23000),('nolboo','jjoogopshae',27800),('nolboo','nolbooboodaeset',23000),('outback','babybackribs',37900),('outback','doublemashroomsteak',35900),('outback','toowoombapasta',23900),('ronyroti','gorgonzolapizza',13900),('ronyroti','ricottacheesesalad',10900),('ronyroti','sirloinsteaksalad',13900);
/*!40000 ALTER TABLE `store_menu_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_table`
--

DROP TABLE IF EXISTS `store_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_table` (
  `store_name` varchar(45) NOT NULL,
  `least_price` int(11) DEFAULT '0',
  `store_category` varchar(45) NOT NULL,
  `phone_num` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`store_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_table`
--

LOCK TABLES `store_table` WRITE;
/*!40000 ALTER TABLE `store_table` DISABLE KEYS */;
INSERT INTO `store_table` VALUES ('alchon',12000,'Korean','02111111'),('aoriramen',9000,'Japanese','02888888'),('bonjuk',8000,'Korean','02222222'),('hoho',5000,'Chinese','02666666'),('hongkong',12000,'Chinese','02444444'),('honydony',8000,'Japanese','02999999'),('kingkebab',12900,'Western','021113333'),('ligarmara',8000,'Chinese','02555555'),('namjasusan',22000,'Japanese','02777777'),('nolboo',15000,'Korean','02333333'),('outback',15000,'Western','021112222'),('ronyloti',15000,'Western','021114444');
/*!40000 ALTER TABLE `store_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_table`
--

DROP TABLE IF EXISTS `user_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_table` (
  `seqNum` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(45) NOT NULL,
  `password` varchar(80) DEFAULT NULL,
  `creationTime` datetime(1) DEFAULT NULL,
  PRIMARY KEY (`seqNum`,`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_table`
--

LOCK TABLES `user_table` WRITE;
/*!40000 ALTER TABLE `user_table` DISABLE KEYS */;
INSERT INTO `user_table` VALUES (1,'cats','*C03B7EE37168C6EC446B0956AF3E2842D7397645','2015-02-23 08:59:46.0'),(2,'임민규','*DCA83ACD74AAE79F91F3592929506563D7FE9F96','2015-02-23 09:00:24.0'),(3,'mlim','*DCA83ACD74AAE79F91F3592929506563D7FE9F96','2015-02-23 09:01:01.0'),(4,'ccslab','*2DF481F2C08CE390E8016973BB43BDDF2AB354F3','2015-02-23 09:01:08.0'),(5,'user-0','*EAAA9D6D415B5AA975BFFA2AF640D8C213E08056','2015-09-15 21:50:01.0'),(6,'user-1','*22236DABAA0E102AD7CB2B1FF04FBD6E62864235','2015-09-15 21:50:16.0'),(7,'user-2','*6B5A2311FEC65B8A85BE980A3B9F04A136A238BD','2015-09-15 21:50:30.0'),(8,'user-3','*D23E6A5496046FA86024BFD157136CD3A0E3B901','2015-09-15 21:50:40.0'),(9,'user-4','*C885112C4BF64543FF8A6BDBF15ADD5D8DE9624E','2015-09-15 21:50:48.0'),(10,'user-5','*10A8D8C9C0E0A9240F494CCD857F2B0BD7E85153','2015-09-15 21:50:57.0'),(11,'user-6','*C0402B262E32AD448D2D40EA6C92641862B5F03A','2015-09-15 21:51:06.0'),(12,'user-7','*667F7092D7A7D86B082EA35C8E20DA48ECA148B3','2015-09-15 21:51:14.0'),(13,'user-8','*E90CF7D778C59C46C6088A2543149FD1417F4E84','2015-09-15 21:51:22.0'),(14,'user-9','*ABCA1B42B2582CE956A0E883C4AB19DB88570F20','2015-09-15 21:51:30.0'),(18,'min','*D7D8497A98023533F9B327EEA245A8E9FB12D963','2021-06-04 14:54:03.0'),(19,'park','*D7D8497A98023533F9B327EEA245A8E9FB12D963','2021-06-04 15:45:52.0');
/*!40000 ALTER TABLE `user_table` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-09  1:39:23
