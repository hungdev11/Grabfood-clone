CREATE DATABASE  IF NOT EXISTS `grab-food` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `grab-food`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: grab-food
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `username` varchar(30) NOT NULL,
  `id_role` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgex1lmaqpg0ir5g1f5eftyaa1` (`username`),
  KEY `FKn2ojv1jm3miwie24w3mop7j1p` (`id_role`),
  CONSTRAINT `FKn2ojv1jm3miwie24w3mop7j1p` FOREIGN KEY (`id_role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'$2a$10$uH.aXcfMhhlmFoR0n3nQNutbAbpZHyUyGgYYuYCCoLCyPG1kTutHK','01111111111',2),(2,'$2a$10$uH.aXcfMhhlmFoR0n3nQNutbAbpZHyUyGgYYuYCCoLCyPG1kTutHK','01111111112',3),(3,'$2a$10$1dMCufmyAGT1Oh9iueiEh./QVK7z5k7zLMfeqxonL8zp26LSqpxhe','012345636',3),(4,'$2a$10$y2DV4CYJJpGUndZGPenm1OOx8HAOyF7l9cqNxgwjyqB9SnKPKk8HW','restaurant6',3),(5,'$2a$10$gj76uK.mdJ5DH5NlbbHKdOxAYwbcQ1cXNVxRWEz3bvYKvEZZIRjoK','0869738540',2),(11,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','shipper01',4),(12,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','shipper02',4),(13,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','shipper03',4),(14,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','shipper04',4),(15,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','shipper05',4),(16,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','user01',2),(17,'$2a$10$UUnDDrEvliAl9Xkp.cgk6Om1qdt/yeMVt2aMPOaRjqnFWEUKugQzG','user03',2),(40,'$2a$10$xiopE1oRfKuaybvlW9qZaOBHfnCSC6lT6jiXviAUZnQzAJ2jTxvDq','hungphangialai11@gmail.com',2),(41,'$2a$10$MMYlRawfiYy9NisjwBq4m.9WriLgXPqT0k150hqUA/8sgG7YxEmlK','restaurant2',3),(42,'$2a$10$XG9HXgawboxILfRnhf2XpeFkQGYCQZMvMYK99.ukEkREazW24O1Dm','restaurant3',3),(43,'$2a$10$SiTvhKln28ItUj3q5eOFWeGYgwLjl3gIYbbYYeu5Gv3uvd5M5QfTq','restaurant4',3),(44,'$2a$10$HqG61qr/6L01YyxZj4HGD.nmBvUG9R63v0PdCbtzuREQBjJrAlqBO','restaurant5',3),(45,'$2a$10$5XdXagGOI2Uj0/DDh3orPOBGYaCdW5T.Y6UEab08fRRsuoe3EpG9e','restaurant7',3),(46,'$2a$10$vDcRZcutFn/wjgiwH7AZYOxYZJLhVrFWK.7sdpHqmpPse8K.bVDom','restaurant8',3),(47,'$2a$10$v9eKeQDoN59GneLKjyMLi.GBJmEt1UzJFXqGlzSEEfoukf352BDHy','restaurant9',3),(48,'$2a$10$xWt9R2YWvDM5RnBrNM4XUODD.tqCw.Tzg1IIhqeawPRHiJNZ51kqe','restaurant10',3),(49,'$2a$10$2W7tq99gLyrf31H6VxfBEOscePiNiy42Bzy9LrU0i9Em5lM9EzS1C','restaurant11',3),(50,'$2a$10$C./ERSHKpJqKEaYIWmmkre6WcqofAhpl0r/Rgt10DhFu7N0vu3o4u','restaurant12',3),(51,'$2a$10$rT0AX7D5d0QVUV3b2KLW5eow7qLNWK65pPC/40pQuE8C7Hv5lzoAy','restaurant13',3),(52,'$2a$10$vrRhZKiYpX6IPIGirQIFrOFPNOf6xCKrM8i7AePcwPInvijj.Ykbe','restaurant14',3),(53,'$2a$10$AVBmiVhuxd2Xipdk1doUlugemoKmuDqg7XjUjLbqD9Ge3HxKW7R.C','restaurant15',3),(54,'$2a$10$ohelxCEwhcCCDdhtZc8oG.1DfwGbrP12o6.2zdxelHi79J0uJhW0O','restaurant16',3),(55,'$2a$10$lanP.uO5y4LYsX8Q8u7R9.v7BuVC6hwtWCW7otOoxtjXVecHj.Oom','lehongphuc24102003@gmail.com',2),(57,'$2a$10$gfJvJN3BitpCblley5WMau3kjGPbNjAn6eDnP2a0Xxx0/bcpQCsiS','dungtranba0512@gmail.com',2),(58,'$2a$10$ayeoNDYgt7Hm3jC9i0CvDuElXUT/z6CZRyG/zLKDcHGmH3jJdPVtS','thanhmuabung@gmail.com',2),(59,'$2a$10$pNbKHpaEYTku7/Th7JgUjOZRqD6Pt8FtX4xBqYNYfe/fy8yVRqriG','0946051206',1),(66,'$2a$10$qBHQEkNz7OM91viAOXl88OrbuBbVUTZWnzkvSRpVzletrl68nT9Zi','0946051202',2),(67,'$2a$10$A4nV871Dst.wH6.iVfN2GO9oCAjExvCp0r96G8q8Yxfkzd/bDHPwe','0946051204',2),(68,'$2a$10$zHfoenW4Z.OhnXpZll85beIlcEJWRrLIisHQ7PbDUrJx/d8DQ6Nzq','123456789',2),(72,'$2a$10$BbrOwFCeJ/AKwr7OXI2BKeOlrJs.OpiVrPgqs.HPc1sMJ0uXRWJgC','00112234',3);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `detail` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `is_default` bit(1) NOT NULL,
  `province` varchar(255) DEFAULT NULL,
  `ward` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKda8tuywtf0gb6sedwk7la1pgi` (`user_id`),
  CONSTRAINT `FKda8tuywtf0gb6sedwk7la1pgi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'97 Man Thiện','Thành phố Thủ Đức',_binary '\0','TP. Hồ Chí Minh','Phường Tăng Nhơn Phú A',NULL,10.8477107,106.7856729),(2,'','Hoan Kiem 1',_binary '\0','HCM','Hang Bai',NULL,10.7614399,106.789),(3,'','Thủ Đức',_binary '\0','HCM','Tăng Nhơn Phú A',NULL,10.86,106.797),(4,'','Thủ Đức',_binary '\0','HCM','Phước Long B',NULL,10.863,106.785),(5,'','Quận 9',_binary '\0','HCM','Long Bình',NULL,10.8874,106.7523),(6,'','Quận 9',_binary '\0','HCM','Long Phước',NULL,10.8885,106.83),(7,'','Thủ Đức',_binary '\0','HCM','Linh Xuân',NULL,10.906,106.689),(8,'','Biên Hòa',_binary '\0','Đồng Nai','Tam Phước',NULL,10.912,106.855),(9,'','Bình Tân',_binary '\0','HCM','An Lạc',NULL,10.767,106.653),(10,'','Quận 3',_binary '\0','HCM','Phường 6',NULL,10.768,106.703),(11,'','Quận 1',_binary '\0','HCM','Bến Nghé',NULL,10.795,106.699),(12,'','Thủ Đức',_binary '\0','HCM','Linh Trung',NULL,10.8109,106.8276),(13,'','Thủ Đức',_binary '\0','HCM','Linh Đông',NULL,10.8152,106.7498),(14,'','Thủ Đức',_binary '\0','HCM','Tân Phú',NULL,10.8423,106.781),(15,'','Hoan Kiem',_binary '\0','Hanoi','Hang Bai',NULL,10.845,106.789),(16,'','Hoan Kiem 1',_binary '\0','HCM','Hang Bai',NULL,10.845,106.789),(17,'','Thủ Đức',_binary '\0','HCM','Tăng Nhơn Phú B',NULL,10.845,106.789),(18,'','Quận 9',_binary '\0','HCM','Long Thạnh Mỹ',NULL,10.8488,106.7382),(22,'1 Võ Văn Ngân','',_binary '','Thành phố Thủ Đức','Phường Linh Chiểu',7,10.8527906,106.7725584),(23,'280 An Dương Vương','',_binary '\0','Thành phố Hồ Chí Minh','Quận 5',7,10.7614399,106.6821537),(24,'An Dương Vương','',_binary '\0','','Quận 5',7,10.7614399,106.6821537),(26,'Đường Lý Thường Kiệt','Thành phố Hồ Chí Minh',_binary '\0','TP. Hồ Chí Minh','Quận 10',7,10.772539,106.6576534),(33,'97 Man Thiện','Thành phố Thủ Đức',_binary '\0','TP. Hồ Chí Minh','Phường Tăng Nhơn Phú A',7,10.8477744,106.7859501),(34,'Nguyễn Thị Minh Khai','Thành phố Hồ Chí Minh',_binary '\0','TP. Hồ Chí Minh','Quận 1',7,10.7896064,106.7045903),(36,'97, Man Thiện','Thành phố Thủ Đức',_binary '\0','Thành phố Hồ Chí Minh','Phường Tăng Nhơn Phú A',13,10.8477107,106.7856729),(37,'Đường số 154','Thành phố Thủ Đức',_binary '','Thành phố Hồ Chí Minh','Phường Tân Phú',13,10.8662213,106.8100217),(38,'2/4, Trần Xuân Soạn','Thành phố Hồ Chí Minh',_binary '\0','Thành phố Hồ Chí Minh','Quận 7',13,10.7516426,106.7204588),(39,'97 Man Thiện','Thành phố Thủ Đức',_binary '','TP. Hồ Chí Minh','Phường Tăng Nhơn Phú A',5,10.8477107,106.7856729),(40,'Nguyễn Thị Minh Khai','Thành phố Hồ Chí Minh',_binary '\0','TP. Hồ Chí Minh','Quận 1',5,10.7896064,106.7045903),(41,'Đường số 154','Thành phố Thủ Đức',_binary '\0','TP. Hồ Chí Minh','Phường Tân Phú',5,10.8662213,106.8100217),(42,'Hẻm 367 Đinh Bộ Lĩnh','Thành phố Hồ Chí Minh',_binary '\0','TP. Hồ Chí Minh','Quận Bình Thạnh',5,10.8148747,106.7114714),(44,'Đường An Dương Vương','Hà Nội',_binary '\0','','Quận Tây Hồ',5,21.0978819,105.8230068),(45,'202A2 Man Thiện','Thành phố Thủ Đức',_binary '\0','TP. Hồ Chí Minh','Phường Tăng Nhơn Phú A',5,10.8515292,106.7947773),(53,'97 Man Thiện','Thành phố Thủ Đức',_binary '\0','TP. Hồ Chí Minh','Phường Tăng Nhơn Phú A',NULL,10.8477107,106.7856729),(55,'','Thành phố Thủ Đức',_binary '\0','TP. Hồ Chí Minh','Phường Tân Phú',NULL,10.8652241,106.8105152),(56,'97 Man Thiện','Thành phố Thủ Đức',_binary '','TP. Hồ Chí Minh','Phường Tăng Nhơn Phú A',6,10.8477107,106.7856729),(61,'101 Lê Lợi','Quận 1',_binary '','TP.HCM','Bến Nghé',21,10.7769,106.7009),(62,'202 Hai Bà Trưng','Quận 3',_binary '\0','TP.HCM','Phường 6',21,10.785,106.6969),(63,'15 Nguyễn Huệ','Quận 1',_binary '','TP.HCM','Bến Nghé',22,10.774,106.7047),(64,'89 Pasteur','Quận 1',_binary '','TP.HCM','Bến Nghé',23,10.7765,106.6992);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9emlp6m95v5er2bcqkjsw48he` (`user_id`),
  CONSTRAINT `FKl70asp4l4w0jmbm1tqyofho4o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,1),(2,2),(3,3),(5,5),(6,6),(12,12),(13,13),(14,14);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_detail`
--

DROP TABLE IF EXISTS `cart_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ids` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `cart_id` bigint DEFAULT NULL,
  `food_id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqf73srg68fdadeq7qvwmbx41g` (`cart_id`,`food_id`,`order_id`,`ids`),
  KEY `FKrvqg5i1h18w17ocen2du1rov` (`food_id`),
  KEY `FK80xo4tytr8ob4eg7812miekks` (`order_id`),
  CONSTRAINT `FK80xo4tytr8ob4eg7812miekks` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKrg4yopd2252nwj8bfcgq5f4jp` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
  CONSTRAINT `FKrvqg5i1h18w17ocen2du1rov` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_detail`
--

LOCK TABLES `cart_detail` WRITE;
/*!40000 ALTER TABLE `cart_detail` DISABLE KEYS */;
INSERT INTO `cart_detail` VALUES (7,'40,41','',5,2,38,6),(8,'41','',1,2,37,6),(9,'','',13,2,38,7),(10,'','',15,2,38,8),(11,'','',13,2,38,21),(12,'','',1,2,38,22),(13,'40','',5,2,38,22),(14,'40','',10,2,38,23),(17,'40,41','',12,2,38,24),(18,'40,41','',9,2,38,25),(20,'','',13,2,38,26),(21,'40,41','',12,2,38,27),(22,'40,41','',9,2,38,28),(23,'40,41','',1,2,38,29),(24,'','',1,2,38,30),(25,'','',1,2,38,31),(26,'','',1,2,38,32),(27,'40,41','',4,2,38,33),(28,'','',2,2,39,33),(29,'','',1,2,38,34),(30,'','',1,2,39,34),(31,'','',1,2,38,35),(32,'','',1,2,38,36),(33,'40','',1,2,38,37),(34,'','',1,2,38,38),(35,'','',1,2,38,39),(36,'','',1,2,38,40),(37,'','',1,2,39,41),(38,'','',1,2,38,41),(40,'40,41','',1,2,38,43),(41,'40,41','',1,2,38,44),(42,'','',1,2,38,45),(43,'','',1,2,38,46),(44,'','',1,2,38,47),(45,'','',1,2,38,48),(46,'','',1,2,37,49),(47,'','',1,2,38,50),(48,'','',1,2,38,51),(49,'','',1,2,38,52),(53,'','',2,2,38,57),(54,'40,41','',10,2,38,59),(55,'40,41','',1,2,38,60),(56,'','',1,2,38,61),(57,'','',1,2,38,62),(58,'40,41','',10,2,38,63),(59,'','',1,2,38,70),(60,'','',1,2,39,70),(61,'','',1,2,37,70),(62,'','',1,2,38,74),(63,'','',1,2,38,75),(64,'','',1,2,39,75),(65,'','',1,2,38,76),(66,'','',1,2,39,76),(67,'40,41','',4,2,38,77),(68,'','',1,2,38,78),(69,'40,41','',1,2,38,79),(70,'40,41','',6,2,38,81),(71,'','',2,2,42,82),(76,'40,41','',5,5,38,84),(77,'41','',6,5,37,84),(78,'40,41','',2,5,37,85),(79,'40,41','',5,5,38,100),(80,'40','',10,5,38,100),(81,'','',2,5,39,100),(82,'40,41','',6,6,37,132),(86,'40,41','',4,6,38,132),(87,'40,41','',3,1,38,NULL),(88,'40','',1,1,38,NULL),(89,'40,41','',4,13,37,97),(90,'40','',3,13,38,97),(91,'40,41','',4,13,37,98),(92,'41','',5,13,38,98),(93,'40','',3,13,37,98),(94,'40,41','',14,13,37,99),(95,'','',3,13,39,99),(96,'41','',4,13,38,99),(97,'40,41','',3,5,38,101),(107,'','',3,5,38,102),(108,'40,41','',7,5,38,102),(109,'41','',12,5,38,115),(110,'40,41','',7,5,37,116),(111,'40,41','',6,5,38,116),(112,'40,41','',6,5,38,117),(113,'','',2,5,38,118),(114,'40,41','',13,5,38,119),(115,'','',3,5,37,120),(116,'','',4,5,37,122),(117,'','',1,5,37,128),(118,'40,41','',3,13,38,125),(119,'40,41','',3,13,37,125),(120,'','',8,13,37,126),(121,'40,41','',5,13,38,127),(122,'40,41','',5,13,37,NULL),(123,'40,41','',7,5,38,128),(124,'40,41','',7,5,38,129),(125,'40,41','',11,5,38,130),(126,'40,41','',5,6,38,133),(127,'40,41','',4,5,37,134),(128,'40','',3,5,38,NULL);
/*!40000 ALTER TABLE `cart_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food`
--

DROP TABLE IF EXISTS `food`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(255) NOT NULL,
  `kind` enum('ADDITIONAL','BOTH','MAIN') NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm9xrxt95wwp1r2s7andom1l1c` (`restaurant_id`),
  KEY `FK1xxp5osqkx3gxmdfqsqwhm1fw` (`type_id`),
  CONSTRAINT `FK1xxp5osqkx3gxmdfqsqwhm1fw` FOREIGN KEY (`type_id`) REFERENCES `food_type` (`id`),
  CONSTRAINT `FKm9xrxt95wwp1r2s7andom1l1c` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food`
--

LOCK TABLES `food` WRITE;
/*!40000 ALTER TABLE `food` DISABLE KEYS */;
INSERT INTO `food` VALUES (37,'abc','https://i.ytimg.com/vi/8w0psAnY6nE/maxresdefault.jpg','MAIN','Banh xeo trung','ACTIVE',7,3),(38,'abc','https://dienmaythiennamhoa.vn/static/images/cach-lam-banh-xeo-mien-bac.jpg','MAIN','Banh xeo tôm','ACTIVE',7,3),(39,'abc','https://www.hacoconut.com/assets/resized/720/banh-xeo.jpg','MAIN','Banh xeo thịt','ACTIVE',7,3),(40,'abc','https://herbario.vn/data/upload_file/Image/nhan-thang5/an-rau-diep-nhieu-cy-tot-khong.jpg','ADDITIONAL','Diếp cá','ACTIVE',7,4),(41,'abc','https://th.bing.com/th/id/OIP.GZdmtONsiUJHIzUj6pJBMwHaEL?rs=1&pid=ImgDetMain','ADDITIONAL','Xà lách','ACTIVE',7,4),(42,'abc','https://xebanhmithonhiky.vn/wp-content/uploads/2021/11/banh-mi-vung-den-han-quoc.jpg','MAIN','Banh mi vung den','ACTIVE',8,1),(59,'abcd','https://comngonsaigon.com/wp-content/uploads/2022/11/com-them.png','MAIN','Com','ACTIVE',8,15);
/*!40000 ALTER TABLE `food` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_detail`
--

DROP TABLE IF EXISTS `food_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) DEFAULT NULL,
  `price` decimal(9,2) NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `food_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnw01v7vq7bc97qbdsnx0sfs48` (`food_id`),
  CONSTRAINT `FKnw01v7vq7bc97qbdsnx0sfs48` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_detail`
--

LOCK TABLES `food_detail` WRITE;
/*!40000 ALTER TABLE `food_detail` DISABLE KEYS */;
INSERT INTO `food_detail` VALUES (13,NULL,6000.00,'2025-04-11 16:19:51.890883',37),(14,NULL,8000.00,'2025-04-11 16:26:39.763991',38),(15,NULL,8000.00,'2025-04-11 16:26:55.893535',39),(16,NULL,2000.00,'2025-04-11 16:27:42.377812',40),(17,NULL,2000.00,'2025-04-11 16:27:56.328044',41),(18,NULL,7000.00,'2025-04-11 16:28:54.670692',42),(19,NULL,5500.00,'2025-04-11 16:28:54.670692',59);
/*!40000 ALTER TABLE `food_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_main_addtion_detail`
--

DROP TABLE IF EXISTS `food_main_addtion_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_main_addtion_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `addition_food_id` bigint NOT NULL,
  `main_food_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr2toygekjlvvwtwvpg9e6ax5p` (`addition_food_id`),
  KEY `FKe2p2y9j6dy39ty0raht9toukt` (`main_food_id`),
  CONSTRAINT `FKe2p2y9j6dy39ty0raht9toukt` FOREIGN KEY (`main_food_id`) REFERENCES `food` (`id`),
  CONSTRAINT `FKr2toygekjlvvwtwvpg9e6ax5p` FOREIGN KEY (`addition_food_id`) REFERENCES `food` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_main_addtion_detail`
--

LOCK TABLES `food_main_addtion_detail` WRITE;
/*!40000 ALTER TABLE `food_main_addtion_detail` DISABLE KEYS */;
INSERT INTO `food_main_addtion_detail` VALUES (5,40,37),(6,41,37),(7,40,38),(8,41,38);
/*!40000 ALTER TABLE `food_main_addtion_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_type`
--

DROP TABLE IF EXISTS `food_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi7ixk9wgjq3ufy4xaf75cm8q3` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_type`
--

LOCK TABLES `food_type` WRITE;
/*!40000 ALTER TABLE `food_type` DISABLE KEYS */;
INSERT INTO `food_type` VALUES (1,'Bánh mì'),(3,'Bánh xèo'),(15,'Cơm'),(2,'Phở'),(4,'Rau');
/*!40000 ALTER TABLE `food_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `body` text NOT NULL,
  `date` datetime(6) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `type` enum('NEW_ORDER','ORDER_STATUS_CHANGED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'Mã đơn #97 , kiểm tra ngay!!!','2025-05-19 18:14:18.868392','Có đơn hàng mới','NEW_ORDER'),(2,'Mã đơn #98 , kiểm tra ngay!!!','2025-05-19 21:15:34.246303','Có đơn hàng mới','NEW_ORDER'),(3,'Mã đơn #99 , kiểm tra ngay!!!','2025-05-19 21:42:29.159289','Có đơn hàng mới','NEW_ORDER'),(4,'Mã đơn #100 , kiểm tra ngay!!!','2025-05-20 20:23:51.257951','Có đơn hàng mới','NEW_ORDER'),(5,'Mã đơn #101 , kiểm tra ngay!!!','2025-05-20 22:59:51.371152','Có đơn hàng mới','NEW_ORDER'),(6,'Mã đơn #102 , kiểm tra ngay!!!','2025-05-21 11:00:45.465934','Có đơn hàng mới','NEW_ORDER'),(7,'Mã đơn #115 , kiểm tra ngay!!!','2025-05-21 15:37:41.300048','Có đơn hàng mới','NEW_ORDER'),(8,'Mã đơn #125 , kiểm tra ngay!!!','2025-05-29 09:13:52.328090','Có đơn hàng mới','NEW_ORDER'),(9,'Mã đơn #126 , kiểm tra ngay!!!','2025-05-29 09:23:37.725398','Có đơn hàng mới','NEW_ORDER'),(10,'Mã đơn #127 , kiểm tra ngay!!!','2025-05-29 09:26:20.049422','Có đơn hàng mới','NEW_ORDER'),(11,'Mã đơn #128 , kiểm tra ngay!!!','2025-05-29 15:53:55.831641','Có đơn hàng mới','NEW_ORDER'),(12,'Mã đơn #129 , kiểm tra ngay!!!','2025-05-29 16:28:55.805872','Có đơn hàng mới','NEW_ORDER'),(13,'Mã đơn #130 , kiểm tra ngay!!!','2025-05-29 16:29:55.045541','Có đơn hàng mới','NEW_ORDER'),(14,'Mã đơn #132 , kiểm tra ngay!!!','2025-05-31 09:50:07.180703','Có đơn hàng mới','NEW_ORDER'),(15,'Mã đơn #133 , kiểm tra ngay!!!','2025-05-31 09:53:49.023001','Có đơn hàng mới','NEW_ORDER'),(16,'Đơn hàng #133 của bạn đã bị từ chối. Vui lòng kiểm tra lại.','2025-05-31 10:06:05.406583','Đơn hàng bị từ chối','ORDER_STATUS_CHANGED'),(17,'Đơn hàng #130 của bạn đang được xử lý. Vui lòng chờ xác nhận.','2025-05-31 10:14:53.337629','Đơn hàng đang được xử lý','ORDER_STATUS_CHANGED'),(18,'Đơn hàng #132 đang trên đường giao đến bạn. Theo dõi đơn hàng nhé!','2025-05-31 10:15:05.116330','Đơn hàng đang được giao','ORDER_STATUS_CHANGED'),(19,'Mã đơn #134 , kiểm tra ngay!!!','2025-05-31 22:36:37.585293','Có đơn hàng mới','NEW_ORDER');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_assignment`
--

DROP TABLE IF EXISTS `order_assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_at` datetime(6) NOT NULL,
  `estimated_delivery_time` datetime(6) DEFAULT NULL,
  `estimated_pickup_time` datetime(6) DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  `responded_at` datetime(6) DEFAULT NULL,
  `status` enum('ACCEPTED','ASSIGNED','CANCELLED','EXPIRED','REJECTED') NOT NULL,
  `order_id` bigint NOT NULL,
  `shipper_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_assignment_status` (`status`,`assigned_at`),
  KEY `FKlqwjttuisb6oywxdy8sgd46o5` (`order_id`),
  KEY `FKhvelawjcknv4br6qgmindmor2` (`shipper_id`),
  CONSTRAINT `FKhvelawjcknv4br6qgmindmor2` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`),
  CONSTRAINT `FKlqwjttuisb6oywxdy8sgd46o5` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_assignment`
--

LOCK TABLES `order_assignment` WRITE;
/*!40000 ALTER TABLE `order_assignment` DISABLE KEYS */;
INSERT INTO `order_assignment` VALUES (1,'2025-06-06 21:29:24.000000','2025-06-06 21:59:24.000000','2025-06-06 21:39:24.000000',NULL,NULL,'ASSIGNED',11,11),(2,'2025-06-06 21:29:24.000000','2025-06-06 21:49:24.000000','2025-06-06 21:34:24.000000',NULL,NULL,'ACCEPTED',12,12),(3,'2025-06-06 21:29:24.000000','2025-06-06 22:09:24.000000','2025-06-06 21:37:24.000000',NULL,NULL,'ASSIGNED',13,13),(4,'2025-06-06 21:29:24.000000','2025-06-06 21:54:24.000000','2025-06-06 21:36:24.000000',NULL,NULL,'ASSIGNED',14,14),(5,'2025-06-06 21:29:24.000000','2025-06-06 21:59:24.000000','2025-06-06 21:39:24.000000','Khách hủy','2025-06-06 21:29:24.000000','CANCELLED',15,15),(6,'2025-06-06 21:29:24.000000','2025-06-06 21:54:24.000000','2025-06-06 21:36:24.000000',NULL,NULL,'ASSIGNED',16,12),(7,'2025-06-06 21:29:24.000000','2025-06-06 21:59:24.000000','2025-06-06 21:39:24.000000',NULL,NULL,'ACCEPTED',17,11),(8,'2025-06-06 21:29:24.000000','2025-06-06 21:44:24.000000','2025-06-06 21:34:24.000000',NULL,NULL,'ASSIGNED',18,15),(9,'2025-06-06 21:29:24.000000','2025-06-06 21:49:24.000000','2025-06-06 21:37:24.000000',NULL,NULL,'ACCEPTED',19,13),(11,'2025-06-06 21:29:24.000000','2025-06-06 21:51:24.000000','2025-06-06 21:35:24.000000','Shipper bận','2025-06-06 21:29:24.000000','REJECTED',20,15);
/*!40000 ALTER TABLE `order_assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_voucher`
--

DROP TABLE IF EXISTS `order_voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_voucher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `time_applied` datetime(6) NOT NULL,
  `order_id` bigint NOT NULL,
  `voucher_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsrhuwor1f2tjthp6k4tmuwrqa` (`order_id`),
  KEY `FK1p6lpshu5gkwycoqypno659at` (`voucher_id`),
  CONSTRAINT `FK1p6lpshu5gkwycoqypno659at` FOREIGN KEY (`voucher_id`) REFERENCES `voucher_detail` (`id`),
  CONSTRAINT `FKsrhuwor1f2tjthp6k4tmuwrqa` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_voucher`
--

LOCK TABLES `order_voucher` WRITE;
/*!40000 ALTER TABLE `order_voucher` DISABLE KEYS */;
INSERT INTO `order_voucher` VALUES (1,'2025-04-15 01:10:27.554662',23,10),(2,'2025-04-15 01:10:27.638192',23,11),(3,'2025-04-15 02:00:02.953247',24,12),(4,'2025-04-15 02:00:02.972200',24,9),(5,'2025-04-15 02:03:03.779834',25,11),(6,'2025-04-15 02:03:03.800740',25,10),(8,'2025-04-15 23:59:18.322816',26,11),(9,'2025-04-16 17:02:13.950839',27,10),(10,'2025-04-16 17:02:13.987742',27,12),(12,'2025-04-17 18:34:47.494172',59,10),(13,'2025-04-17 18:34:47.567270',59,11),(14,'2025-04-17 22:13:12.594523',63,9),(15,'2025-04-17 22:13:12.612895',63,12),(16,'2025-04-18 00:39:08.311460',81,11),(19,'2025-05-21 11:00:45.399224',102,9),(20,'2025-05-21 15:37:41.233212',115,9),(21,'2025-05-21 15:38:47.732217',116,10),(22,'2025-05-21 15:41:06.286777',117,11),(23,'2025-05-21 15:45:51.004899',119,10),(24,'2025-05-29 15:53:55.736235',128,11),(25,'2025-05-29 16:23:21.194483',129,11),(26,'2025-05-29 16:29:32.095060',130,9);
/*!40000 ALTER TABLE `order_voucher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `order_date` datetime(6) NOT NULL,
  `shipping_fee` decimal(9,2) NOT NULL,
  `status` enum('CANCELLED','CANCELLED_REJECTED','COMPLETED','PENDING','REJECTED','PROCESSING','SHIPPING') NOT NULL,
  `total_price` decimal(11,2) NOT NULL,
  `user_id` bigint NOT NULL,
  `discount_order_price` decimal(9,2) NOT NULL,
  `discount_shipping_fee` decimal(9,2) NOT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `distance_km` decimal(5,2) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `picked_up_at` datetime(6) DEFAULT NULL,
  `shipper_earning` decimal(9,2) DEFAULT NULL,
  `tip_amount` decimal(9,2) DEFAULT NULL,
  `shipper_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKel9kyl84ego2otj2accfd8mr7` (`user_id`),
  KEY `FKcw9s4yihuqduodjn391d630i8` (`shipper_id`),
  CONSTRAINT `FKcw9s4yihuqduodjn391d630i8` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`),
  CONSTRAINT `FKel9kyl84ego2otj2accfd8mr7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-12 08:36:23.282346',25000.00,'CANCELLED',34000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-12 08:41:32.519860',25000.00,'CANCELLED',20000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','NOTE OK','2025-04-12 08:44:09.425076',25000.00,'CANCELLED',98000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'VIETNAM','Test','2025-04-13 17:56:48.137207',25000.00,'COMPLETED',57000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,'VIETNAM','Test','2025-04-13 17:59:17.196415',25000.00,'COMPLETED',84000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-13 18:28:16.838475',25000.00,'COMPLETED',110000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,'101 Lê Lợi, Quận 1','Nhanh','2025-06-06 21:28:53.000000',25000.00,'SHIPPING',125000.00,21,5000.00,0.00,NULL,5.20,10.7769,106.7009,'COD',NULL,20000.00,10000.00,11),(12,'202 Hai Bà Trưng, Quận 3','Không hành','2025-06-06 21:28:53.000000',18000.00,'PENDING',95000.00,22,3000.00,0.00,NULL,2.10,10.785,106.6969,'VNPAY',NULL,15000.00,0.00,12),(13,'15 Nguyễn Huệ, Quận 1','Giờ hành chính','2025-06-06 21:28:53.000000',20000.00,'PROCESSING',119000.00,22,7000.00,0.00,NULL,3.30,10.774,106.7047,'COD',NULL,15000.00,15000.00,13),(14,'89 Pasteur, Quận 1','','2025-06-06 21:28:53.000000',25000.00,'COMPLETED',105000.00,23,0.00,0.00,'2025-06-06 21:28:53.000000',1.50,10.7765,106.6992,'COD','2025-06-06 21:28:53.000000',20000.00,10000.00,14),(15,'101 Lê Lợi, Quận 1','','2025-06-06 21:28:53.000000',22000.00,'REJECTED',115000.00,23,0.00,0.00,NULL,4.00,10.7769,106.7009,'VNPAY',NULL,0.00,0.00,15),(16,'25 Nguyễn Thị Minh Khai, Q1','Lấy đúng món','2025-06-06 21:28:53.000000',20000.00,'PENDING',85000.00,22,5000.00,2000.00,NULL,3.10,10.7811,106.7002,'COD',NULL,10000.00,5000.00,12),(17,'47 Hoàng Sa, Q1','','2025-06-06 21:28:53.000000',25000.00,'PROCESSING',135000.00,21,0.00,0.00,NULL,5.70,10.779,106.7077,'VNPAY',NULL,25000.00,15000.00,11),(18,'17 Trần Quang Khải, Q1','Chú ý khách gọi','2025-06-06 21:28:53.000000',18000.00,'SHIPPING',92000.00,23,2000.00,0.00,NULL,2.90,10.7777,106.7055,'COD',NULL,12000.00,10000.00,15),(19,'32 Lý Tự Trọng, Q1','Giao sớm','2025-06-06 21:28:53.000000',15000.00,'COMPLETED',76000.00,21,0.00,0.00,'2025-06-06 21:28:53.000000',2.00,10.7745,106.6985,'COD','2025-06-06 21:28:53.000000',7000.00,3000.00,13),(20,'8 Lê Thánh Tôn, Q1','Khách chuyển khoản','2025-06-06 21:28:53.000000',22000.00,'SHIPPING',101000.00,22,7000.00,0.00,NULL,3.40,10.78,106.7035,'VNPAY',NULL,18000.00,9000.00,14),(21,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-13 18:41:37.521231',25000.00,'PENDING',67800.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(22,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-13 22:49:33.837660',25000.00,'PENDING',48000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(23,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-15 01:10:27.336614',25000.00,'PENDING',80000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(24,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-15 02:00:02.899393',10000.00,'PENDING',129000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(25,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-15 02:03:03.763515',15000.00,'PENDING',98000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(26,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-15 23:59:18.286911',10000.00,'PENDING',94000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-16 17:02:13.890013',15000.00,'PENDING',129000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(28,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:11:30.863127',25000.00,'PENDING',108000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(29,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:13:50.214618',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(30,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:17:29.168497',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(31,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:18:26.938774',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(32,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:20:58.841945',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(33,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:24:47.208769',25000.00,'PENDING',64000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(34,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:26:21.574060',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(35,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:29:17.720693',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(36,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:29:42.042663',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(37,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:29:57.121926',25000.00,'PENDING',10000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(38,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:31:30.663548',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(39,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:40:04.721001',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(40,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:54:00.158205',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(41,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:54:58.102680',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(43,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:05:06.934570',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(44,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:05:34.204662',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(45,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:07:17.614060',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(46,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:07:33.849261',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(47,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:07:49.124522',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(48,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:08:00.935418',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(49,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:08:14.069384',25000.00,'PENDING',6000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(50,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:08:53.860731',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(51,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:09:08.056348',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(52,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:09:25.986556',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(57,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 17:03:30.074877',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(59,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 18:34:47.449215',15000.00,'PENDING',110000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(60,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 18:39:31.104727',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(61,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 22:04:01.485746',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(62,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 22:09:13.564844',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(63,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 22:13:12.583925',10000.00,'PENDING',105000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(70,'VIETNAM','Test','2025-04-17 23:41:34.174423',25000.00,'PENDING',22000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(74,'VIETNAM','Test','2025-04-18 00:04:07.179244',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(75,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:10:52.142544',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(76,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:20:23.962244',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(77,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:23:18.142211',25000.00,'PENDING',48000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(78,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:25:00.423851',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(79,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:25:30.197508',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(81,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:39:08.266991',25000.00,'REJECTED',62000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(82,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-19 07:33:52.786243',25000.00,'REJECTED',14000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(84,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-25 15:40:52.666007',25000.00,'PENDING',108000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(85,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-27 16:04:07.667889',25000.00,'PENDING',20000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(97,'Học viện Công nghệ Bưu chính Viễn thông - Cơ sở TP.HCM, 97, Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, Thành phố Hồ Chí Minh, 71300, Việt Nam','App Order','2025-05-19 18:14:18.771948',25000.00,'PENDING',70000.00,13,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(98,'2/4, Trần Xuân Soạn, Quận 7, Thành phố Hồ Chí Minh, Thành phố Hồ Chí Minh','App Order','2025-05-19 21:15:34.217014',25000.00,'PENDING',114000.00,13,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(99,'Đường số 154, Phường Tân Phú, Thành phố Thủ Đức, Thành phố Hồ Chí Minh','App Order','2025-05-19 21:42:29.135026',25000.00,'PENDING',204000.00,13,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(100,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-20 20:23:51.149908',25000.00,'PENDING',176000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(101,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-20 22:59:51.304648',25000.00,'PENDING',36000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(102,'Đường số 154, Phường Tân Phú, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-21 11:00:45.382881',10000.00,'PENDING',108000.00,5,0.00,15000.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(115,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-21 15:37:41.201251',10000.00,'PENDING',120000.00,5,0.00,15000.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(116,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-21 15:38:47.724681',15000.00,'PENDING',142000.00,5,0.00,10000.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(117,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-21 15:41:06.270030',25000.00,'PENDING',62000.00,5,10000.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(118,'Nguyễn Thị Minh Khai, Quận 1, Thành phố Hồ Chí Minh, TP. Hồ Chí Minh','','2025-05-21 15:43:33.590695',25000.00,'PENDING',16000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(119,'Nguyễn Thị Minh Khai, Quận 1, Thành phố Hồ Chí Minh, TP. Hồ Chí Minh','','2025-05-21 15:45:51.004899',15000.00,'PENDING',156000.00,5,0.00,10000.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(120,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-21 15:48:51.097042',25000.00,'PENDING',18000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(122,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-21 15:51:55.987921',25000.00,'PENDING',24000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(125,'Đường số 154, Phường Tân Phú, Thành phố Thủ Đức, Thành phố Hồ Chí Minh','App Order','2025-05-29 09:13:52.206510',25000.00,'PENDING',66000.00,13,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(126,'97, Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, Thành phố Hồ Chí Minh','App Order','2025-05-29 09:23:37.600167',25000.00,'PENDING',48000.00,13,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(127,'2/4, Trần Xuân Soạn, Quận 7, Thành phố Hồ Chí Minh, Thành phố Hồ Chí Minh','App Order','2025-05-29 09:26:20.029508',25000.00,'PENDING',60000.00,13,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(128,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-29 15:53:55.718064',13000.00,'PENDING',80000.00,5,10000.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(129,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-29 16:23:21.146138',13000.00,'PENDING',62000.00,5,10000.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(130,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-29 16:29:32.087520',0.00,'PROCESSING',132000.00,5,0.00,13000.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(132,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-31 09:49:41.351215',13000.00,'COMPLETED',108000.00,6,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(133,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-31 09:51:58.516904',13000.00,'REJECTED',60000.00,6,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(134,'97 Man Thiện, Phường Tăng Nhơn Phú A, Thành phố Thủ Đức, TP. Hồ Chí Minh','','2025-05-31 22:36:37.515228',13000.00,'PENDING',40000.00,5,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqm42b3i63ge5g0a4fb20vdrnw` (`account_id`),
  CONSTRAINT `FKipbgvnxxexjrflo4n6xli458f` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
INSERT INTO `password_reset_token` VALUES (4,'2025-04-29 18:31:51.383977','41d53811-541c-4a65-b044-cd5217c00eea',58),(5,'2025-04-29 18:42:52.938855','0d734931-e027-473c-bbdb-ec6d52fc93d4',59);
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_info`
--

DROP TABLE IF EXISTS `payment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_at` datetime(6) DEFAULT NULL,
  `paymentamount` decimal(38,2) DEFAULT NULL,
  `payment_code` varchar(255) DEFAULT NULL,
  `payment_name` varchar(255) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlvi5j82l41gxfinwo8npi37qc` (`order_id`),
  CONSTRAINT `FKlvi5j82l41gxfinwo8npi37qc` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
INSERT INTO `payment_info` VALUES (1,'2025-04-17 22:09:57.874128',33000.00,'1331db78-d508-4bd6-9030-766b220c1409','MOMO','SUCCESS',62),(2,'2025-04-17 22:15:37.375063',115000.00,'4cb74a06-7f7a-41dc-b2e5-b0b9188fa9cb','MOMO','SUCCESS',63),(8,'2025-04-17 23:41:34.220977',47000.00,'1607c88c-a130-4d44-816b-b2f8927607b2','COD','SUCCESS',70),(12,'2025-04-18 00:04:33.429226',33000.00,'6944ed91-e211-41fd-a204-52b5250fe774','COD','SUCCESS',74),(13,'2025-04-18 00:10:52.161009',41000.00,'09730e32-b98b-4a05-874e-e3f5e2927a4c','COD','SUCCESS',75),(14,'2025-04-18 00:20:23.982190',41000.00,'08ab22e6-9769-4cfb-af9a-c421aa0c5e9e','COD','SUCCESS',76),(15,'2025-04-18 00:23:18.145041',73000.00,'2789db3e-01d5-45f7-91bb-7a32dc4dc5bd','COD','SUCCESS',77),(16,'2025-04-18 00:25:00.428372',33000.00,'e3437bda-8d07-43c8-acce-062687ca1862','COD','SUCCESS',78),(17,'2025-04-18 00:27:13.995393',37000.00,'4548ccc7-98bf-4890-9a9a-aab883a60cb1','MOMO','SUCCESS',79),(18,'2025-04-18 00:40:35.806121',87000.00,'90072e1c-bc62-4681-a3d3-9d770c40f237','MOMO','SUCCESS',81),(20,'2025-04-27 16:04:07.673924',45000.00,'a6a967a9-7bad-41be-8159-37771684a06d','COD','SUCCESS',85),(23,'2025-05-19 18:14:18.778453',95000.00,'dda2e2d7-3f8d-4eb7-a880-d3f4a6488e4a','COD','SUCCESS',97),(24,'2025-05-19 21:15:34.225209',139000.00,'19b2ee3e-7d92-4400-9c57-a9d5171382de','COD','SUCCESS',98),(25,'2025-05-19 21:42:29.139623',229000.00,'38f17231-2e04-4683-b869-4c18379723a1','COD','SUCCESS',99),(26,'2025-05-20 20:23:51.183127',201000.00,'4d2993d5-a1db-436a-962f-5b7494ed800f','COD','SUCCESS',100),(27,'2025-05-20 22:59:51.304648',61000.00,'a471326b-37b9-4a4a-a0c5-7ba503a76f35','COD','SUCCESS',101),(28,'2025-05-21 11:00:45.415802',118000.00,'f4eb802b-6934-41da-a572-48311802a5ad','COD','SUCCESS',102),(29,'2025-05-21 15:37:41.251566',130000.00,'cafe0c4c-2967-4be4-8fec-8e458c5c5918','COD','SUCCESS',115),(30,'2025-05-29 09:13:52.224101',91000.00,'052dd5c7-fe07-4923-bbc1-28a5c762eced','COD','SUCCESS',125),(31,'2025-05-29 09:23:37.622240',73000.00,'cc55a156-af80-4a35-99ed-72364062b5b6','COD','SUCCESS',126),(32,'2025-05-29 09:26:20.033515',85000.00,'87fcfe76-9a38-4f6e-b4b6-b7e33389e8a6','COD','SUCCESS',127),(33,'2025-05-29 15:53:55.754729',93000.00,'ebae82a1-5179-49f1-bd01-4c9933138011','COD','SUCCESS',128),(34,'2025-05-29 16:23:21.199503',75000.00,'468b8bab-613f-4ff6-bc87-b1ce12c7a757','COD','SUCCESS',129),(35,'2025-05-29 16:29:32.098064',132000.00,'82a80023-28c3-4c0d-a9f0-732294c2080c','COD','SUCCESS',130),(36,'2025-05-31 09:50:07.146057',121000.00,'ebb89c65-085d-4758-87d5-db31e42babc5','MOMO','SUCCESS',132),(37,'2025-05-31 09:53:48.986127',73000.00,'5989536f-9266-4d20-a3d1-900721a57ed7','MOMO','SUCCESS',133),(38,'2025-05-31 22:36:37.529581',53000.00,'e5b4e16d-5d23-446a-ae1f-b5787711fb9d','COD','SUCCESS',134);
/*!40000 ALTER TABLE `payment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reminders`
--

DROP TABLE IF EXISTS `reminders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reminders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `is_email_enabled` bit(1) DEFAULT NULL,
  `is_processed` bit(1) DEFAULT NULL,
  `reminder_time` datetime(6) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKab5sg0sov61nqn1q4gsu1g46k` (`user_id`),
  CONSTRAINT `FKab5sg0sov61nqn1q4gsu1g46k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reminders`
--

LOCK TABLES `reminders` WRITE;
/*!40000 ALTER TABLE `reminders` DISABLE KEYS */;
INSERT INTO `reminders` VALUES (2,'Nhac toi an toi',_binary '',_binary '','2025-05-31 17:47:00.000000','Hen gio',5),(10,'123123',_binary '',_binary '','2025-05-31 22:11:00.000000','lan cuoi',5),(12,'rerwe',_binary '',_binary '','2025-05-31 22:29:00.000000','di ngu',5);
/*!40000 ALTER TABLE `reminders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restaurant`
--

DROP TABLE IF EXISTS `restaurant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `closing_hour` time(6) NOT NULL,
  `create_date` date NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `name` varchar(30) NOT NULL,
  `opening_hour` time(6) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `status` enum('ACTIVE','INACTIVE','PENDING','REJECTED') NOT NULL,
  `account_id` bigint DEFAULT NULL,
  `address_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK979xvypjc2lwr1ia4kq77cko0` (`email`),
  UNIQUE KEY `UK2b01rrbfd5g6hklh8ei57uhgn` (`address_id`),
  UNIQUE KEY `UKn219pfkvmxn9tydrxyr0xq5br` (`account_id`),
  CONSTRAINT `FK96q13p1ptpewvus590a8o83xt` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKpt08lhjedg4gagsyk99axqnhh` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant`
--

LOCK TABLES `restaurant` WRITE;
/*!40000 ALTER TABLE `restaurant` DISABLE KEYS */;
INSERT INTO `restaurant` VALUES (7,'23:30:00.000000','2025-04-11','Modern taste, classic roots','res6@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','Urban Flavor','09:00:00.000000','012345636','ACTIVE',3,1),(8,'23:30:00.000000','2025-04-11','Modern taste, classic roots','res1@gmail.com','https://th.bing.com/th/id/OIP.dd1PM0sxMBe8NoqWMt7QeAHaFj?rs=1&pid=ImgDetMain','ABC Bakery','11:00:00.000000','012345637','ACTIVE',4,2),(50,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res2@gmail.com','https://nhahangvanlocphat.vn/images/News/vlp-com-chien-duong-chau-3.jpg','Com Nha','11:00:00.000000','012345632','ACTIVE',41,3),(51,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res3@gmail.com','https://th.bing.com/th/id/OIP.FiEGdkmGTNJwV3F_-PpIAgHaEq?rs=1&pid=ImgDetMain','Bep Viet','11:00:00.000000','012345633','ACTIVE',42,5),(52,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res4@gmail.com','https://toplist.vn/images/800px/quan-que-164557.jpg','Mon Que','11:00:00.000000','012345634','ACTIVE',43,6),(53,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res5@gmail.com','https://tiki.vn/blog/wp-content/uploads/2023/07/thumb-10.jpg','Lau Ngon','11:00:00.000000','012345635','ACTIVE',44,7),(54,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res7@gmail.com','https://daotaobeptruong.vn/wp-content/uploads/2020/11/do-an-vat-han-quoc.jpg','An Vat','11:00:00.000000','012345636','ACTIVE',45,9),(55,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res8@gmail.com','https://wikihuongdan.com/wp-content/uploads/2021/11/bua-com-gia-dinh-viet.jpg','Do An Nha','11:00:00.000000','012345637','ACTIVE',46,10),(56,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res9@gmail.com','https://diadiemlongkhanh.cdn.vccloud.vn/static/images/2022/06/08/ee1023df-7261-4b67-b939-9aef34e0d33e-image.jpeg','Chao Long','11:00:00.000000','012345639','ACTIVE',47,11),(57,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res10@gmail.com','https://th.bing.com/th/id/OIP.Y6nFeTlHIC-wyKyR2Pj_zwHaFJ?rs=1&pid=ImgDetMain','Pho Viet','11:00:00.000000','012345640','ACTIVE',48,12),(58,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res11@gmail.com','https://giadungducsaigon.vn/wp-content/uploads/2022/07/cach-lam-bun-ca-cay-hai-phong.jpg','Bun Ca','11:00:00.000000','012345641','ACTIVE',49,13),(59,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res12@gmail.com','https://i.ytimg.com/vi/A_o2qfaTgKs/maxresdefault.jpg','Bun Bo','11:00:00.000000','012345642','ACTIVE',50,14),(60,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res13@gmail.com','https://th.bing.com/th/id/OIP.DPlAJBRciXLa02HvnGDFrgHaFS?rs=1&pid=ImgDetMain','Com Tam','11:00:00.000000','012345643','ACTIVE',51,15),(62,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res15@gmail.com','https://th.bing.com/th/id/OIP.3qTAZAfvBpleiPRwrLc0GAHaE8?rs=1&pid=ImgDetMain','An Vui','11:00:00.000000','012345645','ACTIVE',53,17),(63,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res16@gmail.com','https://afamilycdn.com/k:thumb_w/600/Qalypm8xccccccccccccW2vZ1VroR/Image/2014/09/02/ANH-6-71d4a/3-cach-trang-tri-mon-an-vui-nhon-dang-yeu.jpg','Bung No','11:00:00.000000','012345638','ACTIVE',54,18),(66,'13:00:00.000000','2025-05-22','Cơm quê giá thành phố','comqueduongbau@gmail.com','https://th.bing.com/th/id/OIP.8miap47PJnAPWnOClxcH0AHaHa?cb=iwp2&rs=1&pid=ImgDetMain','Com que duong bau','08:00:00.000000','00112234','ACTIVE',72,53),(68,'22:00:00.000000','2025-05-30','Ăn đi khỏi mô tả','anlaghien@gmail.com','https://cdn.tgdd.vn/Files/2020/05/05/1253668/2-cach-lam-suon-chien-gion-va-chien-xu-don-gian-thom-ngon-an-la-ghien-10.jpg','Ăn là ghiền','08:00:00.000000','123987456','REJECTED',NULL,55);
/*!40000 ALTER TABLE `restaurant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `review_time` datetime(6) NOT NULL,
  `rating` decimal(38,2) NOT NULL,
  `reply_message` varchar(255) DEFAULT NULL,
  `reply_time` datetime(6) DEFAULT NULL,
  `review_message` varchar(255) DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `order_string` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsbkc1fll14ly5y6yxxk2jwlef` (`order_id`),
  CONSTRAINT `FKqwgq1lxgahsxdspnwqfac6sv6` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rewards`
--

DROP TABLE IF EXISTS `rewards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rewards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `gems_value` int DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `peak_end_time` time(6) DEFAULT NULL,
  `peak_start_time` time(6) DEFAULT NULL,
  `required_deliveries` int DEFAULT NULL,
  `required_distance` float DEFAULT NULL,
  `required_orders` int DEFAULT NULL,
  `required_rating` float DEFAULT NULL,
  `reward_value` decimal(10,2) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','EXPIRED','INACTIVE') NOT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('ACHIEVEMENT','BONUS','DAILY','PEAK_HOUR') DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rewards`
--

LOCK TABLES `rewards` WRITE;
/*!40000 ALTER TABLE `rewards` DISABLE KEYS */;
INSERT INTO `rewards` VALUES (1,50000.00,'admin','Hoàn thành 50 đơn hàng trong tháng','2025-07-01 00:00:00.000000',5,'https://icon.com/gem1.png',_binary '','Reward50',NULL,NULL,50,0,0,4.5,50000.00,'2025-06-01 00:00:00.000000','ACTIVE','Thưởng 50 đơn','ACHIEVEMENT','2025-06-01','2025-07-01'),(2,100000.00,'admin','Top shipper của tuần','2025-07-10 00:00:00.000000',10,'https://icon.com/gem2.png',_binary '','TopWeek',NULL,NULL,0,0,0,4.7,100000.00,'2025-06-01 00:00:00.000000','ACTIVE','Shipper xuất sắc','BONUS','2025-06-01','2025-07-10'),(3,20000.00,'admin','Nhận đánh giá 5 sao từ khách','2025-07-05 00:00:00.000000',2,'https://icon.com/gem3.png',_binary '','Rating5Star',NULL,NULL,0,0,0,5,20000.00,'2025-06-10 00:00:00.000000','ACTIVE','5 sao khách hàng','ACHIEVEMENT','2025-06-10','2025-07-05'),(4,40000.00,'admin','Hoàn thành 20 đơn trong tuần','2025-07-02 00:00:00.000000',3,'https://icon.com/gem4.png',_binary '','Reward20',NULL,NULL,20,0,0,4.2,40000.00,'2025-06-01 00:00:00.000000','ACTIVE','Thưởng 20 đơn tuần','DAILY','2025-06-01','2025-07-02'),(5,200000.00,'admin','Hoàn thành 100km trong tháng','2025-07-31 00:00:00.000000',15,'https://icon.com/gem5.png',_binary '','100kmMonth',NULL,NULL,0,100,0,4,200000.00,'2025-06-01 00:00:00.000000','ACTIVE','Thưởng 100km tháng','ACHIEVEMENT','2025-06-01','2025-07-31'),(6,15000.00,'admin','Đạt 95% tỉ lệ nhận đơn','2025-07-10 00:00:00.000000',2,'https://icon.com/gem6.png',_binary '','Accept95',NULL,NULL,0,0,0,0,15000.00,'2025-06-10 00:00:00.000000','ACTIVE','Tỉ lệ nhận đơn 95%','ACHIEVEMENT','2025-06-10','2025-07-10'),(7,25000.00,'admin','Không bị hủy đơn trong tháng','2025-07-31 00:00:00.000000',3,'https://icon.com/gem7.png',_binary '','NoCancel',NULL,NULL,0,0,0,4.8,25000.00,'2025-06-15 00:00:00.000000','ACTIVE','Không hủy đơn tháng','DAILY','2025-06-15','2025-07-31'),(8,100000.00,'admin','10 lượt khen từ khách hàng trong tuần','2025-06-20 00:00:00.000000',8,'https://icon.com/gem8.png',_binary '','CustomerPraise',NULL,NULL,0,0,10,4.6,100000.00,'2025-06-10 00:00:00.000000','ACTIVE','10 lượt khen','BONUS','2025-06-10','2025-06-20'),(9,5000.00,'admin','Đăng nhập đủ 7 ngày liên tục','2025-07-31 00:00:00.000000',1,'https://icon.com/gem9.png',_binary '','Active7Days',NULL,NULL,0,0,0,0,5000.00,'2025-06-15 00:00:00.000000','ACTIVE','Đăng nhập 7 ngày','DAILY','2025-06-15','2025-07-31'),(10,50000.00,'admin','Hoạt động trong giờ cao điểm','2025-07-10 00:00:00.000000',3,'https://icon.com/gem10.png',_binary '','PeakHourBonus','21:00:00.000000','18:00:00.000000',0,0,0,4.5,50000.00,'2025-06-20 00:00:00.000000','ACTIVE','Thưởng giờ cao điểm','PEAK_HOUR','2025-06-20','2025-07-10');
/*!40000 ALTER TABLE `rewards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER'),(3,'ROLE_RES'),(4,'ROLE_SHIPPER');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipper`
--

DROP TABLE IF EXISTS `shipper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipper` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `acceptance_rate` float NOT NULL,
  `accepted_orders` int NOT NULL,
  `cancellation_rate` float NOT NULL,
  `completed_orders` int NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `current_latitude` double DEFAULT NULL,
  `current_longitude` double DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gems` int NOT NULL,
  `is_online` bit(1) NOT NULL,
  `license_plate` varchar(255) NOT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `rating` decimal(3,2) NOT NULL,
  `rejected_orders` int NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') DEFAULT NULL,
  `total_orders` int NOT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `account_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKndrldc7jwnhuss6rxr0gjg6gm` (`email`),
  UNIQUE KEY `UKlj4yjb586j4658glfwa54i120` (`phone`),
  UNIQUE KEY `UK2uxcy232tkd6p5mtk4530yn0u` (`account_id`),
  KEY `idx_shipper_location` (`current_latitude`,`current_longitude`,`is_online`,`status`),
  KEY `idx_shipper_online_status` (`status`,`is_online`),
  CONSTRAINT `FKckouiteoyj90usbwdw9b3jpg5` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipper`
--

LOCK TABLES `shipper` WRITE;
/*!40000 ALTER TABLE `shipper` DISABLE KEYS */;
INSERT INTO `shipper` VALUES (11,98.5,110,1.2,105,'2025-06-06 21:22:45.000000',10.776,106.7,'shipper01@demo.com',12,_binary '','59A1-12345','2025-06-06 21:22:45.000000','Nguyễn Văn A','0111111111',4.90,2,'ACTIVE',120,'AB1234','motorbike',11),(12,95.2,80,2.1,75,'2025-06-06 21:22:45.000000',10.78,106.71,'shipper02@demo.com',8,_binary '','59B2-23456','2025-06-06 21:22:45.000000','Lê Thị B','0222222222',4.70,3,'ACTIVE',82,'BC2345','motorbike',12),(13,99,60,0.5,60,'2025-06-06 21:22:45.000000',10.77,106.72,'shipper03@demo.com',9,_binary '','59C3-34567','2025-06-06 21:22:45.000000','Trần Văn C','0333333333',5.00,0,'ACTIVE',60,'CD3456','motorbike',13),(14,96,45,3.5,42,'2025-06-06 21:22:45.000000',10.765,106.73,'shipper04@demo.com',5,_binary '','59D4-45678','2025-06-06 21:22:45.000000','Phạm Thị D','0444444444',4.50,5,'INACTIVE',48,'DE4567','motorbike',14),(15,93.5,100,5.2,95,'2025-06-06 21:22:45.000000',10.773,106.725,'shipper05@demo.com',7,_binary '','59E5-56789','2025-06-06 21:22:45.000000','Võ Văn E','0555555555',4.60,6,'ACTIVE',110,'EF5678','motorbike',15);
/*!40000 ALTER TABLE `shipper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipper_rewards`
--

DROP TABLE IF EXISTS `shipper_rewards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipper_rewards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `claimed_at` datetime(6) DEFAULT NULL,
  `completion_percentage` float DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `progress_value` float DEFAULT NULL,
  `status` enum('CLAIMED','ELIGIBLE','EXPIRED') NOT NULL,
  `reward_id` bigint NOT NULL,
  `shipper_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_shipper_reward` (`shipper_id`,`reward_id`),
  KEY `FK4b1otgxqmlupa60uupwf6ocnf` (`reward_id`),
  CONSTRAINT `FK44lm35cseixm3qbp8ssb05jsi` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`),
  CONSTRAINT `FK4b1otgxqmlupa60uupwf6ocnf` FOREIGN KEY (`reward_id`) REFERENCES `rewards` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipper_rewards`
--

LOCK TABLES `shipper_rewards` WRITE;
/*!40000 ALTER TABLE `shipper_rewards` DISABLE KEYS */;
INSERT INTO `shipper_rewards` VALUES (1,'2025-06-06 21:25:03.000000',100,'Hoàn thành 50 đơn',50,'CLAIMED',1,11),(2,'2025-06-06 21:25:03.000000',100,'Top tuần',1,'CLAIMED',2,12),(3,'2025-06-06 21:25:03.000000',100,'Được 5 sao khách hàng',1,'CLAIMED',3,13),(4,NULL,75,'Đã nhận 15/20 đơn',15,'ELIGIBLE',4,14),(5,'2025-06-06 21:25:03.000000',100,'Đi đủ 100km tháng',100,'CLAIMED',5,12),(6,NULL,95,'Đạt 95% nhận đơn',95,'ELIGIBLE',6,13),(7,'2025-06-06 21:25:03.000000',100,'Không bị hủy đơn tháng',30,'CLAIMED',7,11),(8,NULL,70,'Được 7/10 lượt khen',7,'ELIGIBLE',8,15),(9,'2025-06-06 21:25:03.000000',100,'Đăng nhập đủ 7 ngày',7,'CLAIMED',9,14),(10,'2025-06-06 21:25:03.000000',100,'Hoạt động giờ cao điểm',10,'CLAIMED',10,15);
/*!40000 ALTER TABLE `shipper_rewards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` bigint NOT NULL,
  `commission` bigint DEFAULT NULL,
  `delivery_fee` bigint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `net_amount` bigint DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','PENDING') NOT NULL,
  `tip` bigint DEFAULT NULL,
  `transaction_date` datetime(6) NOT NULL,
  `type` enum('BONUS','COD_DEPOSIT','COMMISSION','EARNING','TIP','TOP_UP') NOT NULL,
  `shipper_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ajawi9yo3w0xwwm329irurji` (`shipper_id`),
  CONSTRAINT `FK3ajawi9yo3w0xwwm329irurji` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `account_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK589idila9li6a4arw1t8ht1gx` (`phone`),
  UNIQUE KEY `UKnrrhhb0bsexvi8ch6wnon9uog` (`account_id`),
  CONSTRAINT `FKc3b4xfbq6rbkkrddsdum8t5f0` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2025-04-11 15:38:21.922756','n21dccn126@student.ptithcm.edu.vn','2025-04-11 15:38:21.922756','Phan Phi Hùng','01111111111',1),(2,'2025-04-12 08:34:24.073561','lhphuc.24110@gmail.com','2025-04-12 08:34:24.073561','Le Hong Phuc','0869738540',5),(3,'2025-04-19 07:44:43.578852','lehongphuc24102003@gmail.com','2025-04-19 07:44:43.578852','Phúc Lê','0577017530',55),(5,'2025-04-25 15:17:42.386138','dungtranba0512@gmail.com','2025-04-25 15:17:42.387068','Dũng Trần Bá','0499215516',57),(6,'2025-04-27 16:12:49.061257','thanhmuabung@gmail.com','2025-04-27 16:12:49.061257','Dũng Trần Bá','0859960702',58),(7,'2025-04-28 16:22:49.634066','n21dccn112@student.ptithcm.edu.vn','2025-04-28 16:22:49.634066','tranbadung','0946051206',59),(12,'2025-05-09 10:41:18.987607','dungtranba0513@gmail.com','2025-05-09 10:41:18.987607','dung','0946051202',66),(13,'2025-05-09 10:45:58.910410','dungaksjd@gmail.com','2025-05-09 10:45:58.910410','dung ne','0946051204',67),(14,'2025-05-09 11:24:44.668579','afsfgsfdgsg@gmail.com','2025-05-09 11:24:44.668579','dung nha','123456789',68),(21,'2025-06-06 21:20:19.000000','alice.demo@demo.com','2025-06-06 21:20:19.000000','Alice Demo','0101010101',15),(22,'2025-06-06 21:20:19.000000','bob.demo@demo.com','2025-06-06 21:20:19.000000','Bob Demo','0202020202',16),(23,'2025-06-06 21:20:19.000000','charlie.demo@demo.com','2025-06-06 21:20:19.000000','Charlie Demo','0303030303',17);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_notification`
--

DROP TABLE IF EXISTS `user_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_deleted` bit(1) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `notification_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKeln4r836k3vr3g7e34lwbq59j` (`account_id`,`notification_id`),
  KEY `FKp137d22f65l9kjbqjgfb37oy` (`notification_id`),
  CONSTRAINT `FK8yjtuef1qlo5djvup0tc5b1uj` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
  CONSTRAINT `FKp137d22f65l9kjbqjgfb37oy` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_notification`
--

LOCK TABLES `user_notification` WRITE;
/*!40000 ALTER TABLE `user_notification` DISABLE KEYS */;
INSERT INTO `user_notification` VALUES (1,_binary '\0',_binary '\0',1,3),(2,_binary '\0',_binary '\0',2,3),(3,_binary '\0',_binary '\0',3,3),(4,_binary '\0',_binary '\0',4,3),(5,_binary '\0',_binary '\0',5,3),(6,_binary '\0',_binary '\0',6,3),(7,_binary '\0',_binary '\0',7,3),(8,_binary '\0',_binary '\0',8,3),(9,_binary '\0',_binary '\0',9,3),(10,_binary '\0',_binary '\0',10,3),(11,_binary '\0',_binary '\0',11,3),(12,_binary '\0',_binary '\0',12,3),(13,_binary '\0',_binary '\0',13,3),(14,_binary '\0',_binary '\0',14,3),(15,_binary '\0',_binary '\0',15,3),(16,_binary '\0',_binary '\0',16,58),(17,_binary '\0',_binary '',17,57),(18,_binary '\0',_binary '\0',18,58),(19,_binary '\0',_binary '\0',19,3);
/*!40000 ALTER TABLE `user_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voucher`
--

DROP TABLE IF EXISTS `voucher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voucher` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `min_require` decimal(38,2) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `type` enum('FIXED','PERCENTAGE') NOT NULL,
  `value` decimal(38,2) DEFAULT NULL,
  `restaurant_id` bigint DEFAULT NULL,
  `apply_type` enum('ALL','ORDER','SHIPPING') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKpvh1lqheshnjoekevvwla03xn` (`code`),
  KEY `FK8vaqbcpuuuiuopl2uwpr56uyc` (`restaurant_id`),
  CONSTRAINT `FK8vaqbcpuuuiuopl2uwpr56uyc` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voucher`
--

LOCK TABLES `voucher` WRITE;
/*!40000 ALTER TABLE `voucher` DISABLE KEYS */;
INSERT INTO `voucher` VALUES (14,'GG15KSHIP','Mã giảm giá vận chuyển 15K',100000.00,'ACTIVE','FIXED',15000.00,NULL,'SHIPPING'),(15,'GG10KSHIP','Mã giảm giá vận chuyển 10K',100000.00,'ACTIVE','FIXED',10000.00,NULL,'SHIPPING'),(16,'GG10KORDER','Mã giảm giá đơn hàng 10K',50000.00,'ACTIVE','FIXED',10000.00,NULL,'ORDER'),(18,'GG15KORDER','Mã giảm giá đơn hàng 15K',100000.00,'ACTIVE','FIXED',15000.00,NULL,'ORDER'),(19,'GG5PTORDER','Mã giảm giá đơn hàng 5%',100000.00,'ACTIVE','PERCENTAGE',5.00,NULL,'ORDER');
/*!40000 ALTER TABLE `voucher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voucher_detail`
--

DROP TABLE IF EXISTS `voucher_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voucher_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_date` datetime(6) NOT NULL,
  `start_date` datetime(6) NOT NULL,
  `food_id` bigint DEFAULT NULL,
  `voucher_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3lrovevc9wweesl8jmuwcsf8r` (`food_id`),
  KEY `FKq2bwpsy6xqko0o5oakc1lmj46` (`voucher_id`),
  CONSTRAINT `FK3lrovevc9wweesl8jmuwcsf8r` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`),
  CONSTRAINT `FKq2bwpsy6xqko0o5oakc1lmj46` FOREIGN KEY (`voucher_id`) REFERENCES `voucher` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voucher_detail`
--

LOCK TABLES `voucher_detail` WRITE;
/*!40000 ALTER TABLE `voucher_detail` DISABLE KEYS */;
INSERT INTO `voucher_detail` VALUES (9,'2026-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,14,9),(10,'2026-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,15,10),(11,'2026-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,16,8),(12,'2026-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,18,10),(13,'2026-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,19,5);
/*!40000 ALTER TABLE `voucher_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wallet`
--

DROP TABLE IF EXISTS `wallet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cod_holding` bigint NOT NULL,
  `current_balance` bigint NOT NULL,
  `is_eligible_for_cod` bit(1) NOT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  `month_earnings` bigint NOT NULL,
  `today_earnings` bigint NOT NULL,
  `total_earnings` bigint NOT NULL,
  `week_earnings` bigint NOT NULL,
  `shipper_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKssevn2cngy3wq96vfcnabuevt` (`shipper_id`),
  CONSTRAINT `FKg9i7q6wcj7durxxix1688yby3` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet`
--

LOCK TABLES `wallet` WRITE;
/*!40000 ALTER TABLE `wallet` DISABLE KEYS */;
INSERT INTO `wallet` VALUES (1,100000,1000000,_binary '','2025-06-06 21:22:50.000000',800000,120000,4000000,220000,11),(2,75000,850000,_binary '','2025-06-06 21:22:50.000000',650000,70000,3200000,180000,12),(3,50000,920000,_binary '','2025-06-06 21:22:50.000000',900000,50000,3700000,250000,13),(4,120000,780000,_binary '\0','2025-06-06 21:22:50.000000',600000,90000,2900000,140000,14),(5,90000,990000,_binary '','2025-06-06 21:22:50.000000',720000,100000,4100000,210000,15);
/*!40000 ALTER TABLE `wallet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'grab-food'
--

--
-- Dumping routines for database 'grab-food'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-06 21:33:45
