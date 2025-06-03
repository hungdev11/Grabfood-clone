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
  `is_active` bit(1) NOT NULL,
  `phone` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgex1lmaqpg0ir5g1f5eftyaa1` (`username`),
  KEY `FKn2ojv1jm3miwie24w3mop7j1p` (`id_role`),
  CONSTRAINT `FKn2ojv1jm3miwie24w3mop7j1p` FOREIGN KEY (`id_role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'$2a$10$uH.aXcfMhhlmFoR0n3nQNutbAbpZHyUyGgYYuYCCoLCyPG1kTutHK','01111111111',2,_binary '\0',''),(2,'$2a$10$uH.aXcfMhhlmFoR0n3nQNutbAbpZHyUyGgYYuYCCoLCyPG1kTutHK','01111111112',3,_binary '\0',''),(3,'$2a$10$1dMCufmyAGT1Oh9iueiEh./QVK7z5k7zLMfeqxonL8zp26LSqpxhe','012345636',3,_binary '\0',''),(4,'$2a$10$y2DV4CYJJpGUndZGPenm1OOx8HAOyF7l9cqNxgwjyqB9SnKPKk8HW','restaurant6',3,_binary '\0',''),(5,'$2a$10$gj76uK.mdJ5DH5NlbbHKdOxAYwbcQ1cXNVxRWEz3bvYKvEZZIRjoK','0869738540',2,_binary '\0',''),(11,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0111111111',4,_binary '','0111111111'),(12,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0222222222',4,_binary '','0222222222'),(13,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0333333333',4,_binary '','0333333333'),(14,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0444444444',4,_binary '','0444444444'),(15,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0555555555',4,_binary '','0555555555'),(16,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0666666666',4,_binary '','0666666666'),(17,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0777777777',4,_binary '','0777777777'),(18,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0888888888',4,_binary '','0888888888'),(19,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0999999999',4,_binary '','0999999999'),(20,'$2a$10$uLBDYgMGy5cUKbiRi1VMReFZQ/eekCl3F40O8Ya2n7hzlov7jy4KW','0000000000',4,_binary '','0000000000'),(40,'$2a$10$xiopE1oRfKuaybvlW9qZaOBHfnCSC6lT6jiXviAUZnQzAJ2jTxvDq','hungphangialai11@gmail.com',2,_binary '\0',''),(41,'$2a$10$MMYlRawfiYy9NisjwBq4m.9WriLgXPqT0k150hqUA/8sgG7YxEmlK','restaurant2',3,_binary '\0',''),(42,'$2a$10$XG9HXgawboxILfRnhf2XpeFkQGYCQZMvMYK99.ukEkREazW24O1Dm','restaurant3',3,_binary '\0',''),(43,'$2a$10$SiTvhKln28ItUj3q5eOFWeGYgwLjl3gIYbbYYeu5Gv3uvd5M5QfTq','restaurant4',3,_binary '\0',''),(44,'$2a$10$HqG61qr/6L01YyxZj4HGD.nmBvUG9R63v0PdCbtzuREQBjJrAlqBO','restaurant5',3,_binary '\0',''),(45,'$2a$10$5XdXagGOI2Uj0/DDh3orPOBGYaCdW5T.Y6UEab08fRRsuoe3EpG9e','restaurant7',3,_binary '\0',''),(46,'$2a$10$vDcRZcutFn/wjgiwH7AZYOxYZJLhVrFWK.7sdpHqmpPse8K.bVDom','restaurant8',3,_binary '\0',''),(47,'$2a$10$v9eKeQDoN59GneLKjyMLi.GBJmEt1UzJFXqGlzSEEfoukf352BDHy','restaurant9',3,_binary '\0',''),(48,'$2a$10$xWt9R2YWvDM5RnBrNM4XUODD.tqCw.Tzg1IIhqeawPRHiJNZ51kqe','restaurant10',3,_binary '\0',''),(49,'$2a$10$2W7tq99gLyrf31H6VxfBEOscePiNiy42Bzy9LrU0i9Em5lM9EzS1C','restaurant11',3,_binary '\0',''),(50,'$2a$10$C./ERSHKpJqKEaYIWmmkre6WcqofAhpl0r/Rgt10DhFu7N0vu3o4u','restaurant12',3,_binary '\0',''),(51,'$2a$10$rT0AX7D5d0QVUV3b2KLW5eow7qLNWK65pPC/40pQuE8C7Hv5lzoAy','restaurant13',3,_binary '\0',''),(52,'$2a$10$vrRhZKiYpX6IPIGirQIFrOFPNOf6xCKrM8i7AePcwPInvijj.Ykbe','restaurant14',3,_binary '\0',''),(53,'$2a$10$AVBmiVhuxd2Xipdk1doUlugemoKmuDqg7XjUjLbqD9Ge3HxKW7R.C','restaurant15',3,_binary '\0',''),(54,'$2a$10$ohelxCEwhcCCDdhtZc8oG.1DfwGbrP12o6.2zdxelHi79J0uJhW0O','restaurant16',3,_binary '\0',''),(55,'$2a$10$lanP.uO5y4LYsX8Q8u7R9.v7BuVC6hwtWCW7otOoxtjXVecHj.Oom','lehongphuc24102003@gmail.com',2,_binary '\0',''),(56,'$2a$10$/gTzvmMsYw0iSG/ePs95ouflKGvV8Fy0A.KeappKLcDbpUhr8pT66','lhphuc.2410@gmail.com',2,_binary '\0','');
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'','Hoan Kiem',_binary '\0','Hanoi','Hang Bai',NULL,0,0),(2,'','Hoan Kiem 1',_binary '\0','HCM','Hang Bai',NULL,0,0),(3,'','Thủ Đức',_binary '\0','HCM','Tăng Nhơn Phú A',NULL,10.86,106.797),(4,'','Thủ Đức',_binary '\0','HCM','Phước Long B',NULL,10.863,106.785),(5,'','Quận 9',_binary '\0','HCM','Long Bình',NULL,10.8874,106.7523),(6,'','Quận 9',_binary '\0','HCM','Long Phước',NULL,10.8885,106.83),(7,'','Thủ Đức',_binary '\0','HCM','Linh Xuân',NULL,10.906,106.689),(8,'','Biên Hòa',_binary '\0','Đồng Nai','Tam Phước',NULL,10.912,106.855),(9,'','Bình Tân',_binary '\0','HCM','An Lạc',NULL,10.767,106.653),(10,'','Quận 3',_binary '\0','HCM','Phường 6',NULL,10.768,106.703),(11,'','Quận 1',_binary '\0','HCM','Bến Nghé',NULL,10.795,106.699),(12,'','Thủ Đức',_binary '\0','HCM','Linh Trung',NULL,10.8109,106.8276),(13,'','Thủ Đức',_binary '\0','HCM','Linh Đông',NULL,10.8152,106.7498),(14,'','Thủ Đức',_binary '\0','HCM','Tân Phú',NULL,10.8423,106.781),(15,'','Hoan Kiem',_binary '\0','Hanoi','Hang Bai',NULL,10.845,106.789),(16,'','Hoan Kiem 1',_binary '\0','HCM','Hang Bai',NULL,10.845,106.789),(17,'','Thủ Đức',_binary '\0','HCM','Tăng Nhơn Phú B',NULL,10.845,106.789),(18,'','Quận 9',_binary '\0','HCM','Long Thạnh Mỹ',NULL,10.8488,106.7382);
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,1),(2,2),(3,3),(4,4);
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
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_detail`
--

LOCK TABLES `cart_detail` WRITE;
/*!40000 ALTER TABLE `cart_detail` DISABLE KEYS */;
INSERT INTO `cart_detail` VALUES (7,'40,41','',5,2,38,6),(8,'41','',1,2,37,6),(9,'','',13,2,38,7),(10,'','',15,2,38,8),(11,'','',13,2,38,21),(14,'40','',10,2,38,23),(17,'40,41','',12,2,38,24),(18,'40,41','',9,2,38,25),(23,'40,41','',1,2,38,29),(24,'','',1,2,38,30),(25,'','',1,2,38,31),(26,'','',1,2,38,32),(27,'40,41','',4,2,38,33),(28,'','',2,2,39,33),(29,'','',1,2,38,34),(30,'','',1,2,39,34),(31,'','',1,2,38,35),(32,'','',1,2,38,36),(33,'40','',1,2,38,37),(34,'','',1,2,38,38),(35,'','',1,2,38,39),(36,'','',1,2,38,40),(37,'','',1,2,39,41),(38,'','',1,2,38,41),(40,'40,41','',1,2,38,43),(41,'40,41','',1,2,38,44),(42,'','',1,2,38,45),(43,'','',1,2,38,46),(44,'','',1,2,38,47),(45,'','',1,2,38,48),(46,'','',1,2,37,49),(47,'','',1,2,38,50),(48,'','',1,2,38,51),(49,'','',1,2,38,52),(53,'','',2,2,38,57),(54,'40,41','',10,2,38,59),(55,'40,41','',1,2,38,60),(56,'','',1,2,38,61),(57,'','',1,2,38,62),(58,'40,41','',10,2,38,63),(59,'','',1,2,38,70),(60,'','',1,2,39,70),(61,'','',1,2,37,70),(62,'','',1,2,38,74),(63,'','',1,2,38,75),(64,'','',1,2,39,75),(65,'','',1,2,38,76),(66,'','',1,2,39,76),(67,'40,41','',4,2,38,77),(68,'','',1,2,38,78),(69,'40,41','',1,2,38,79),(70,'40,41','',6,2,38,81),(71,'','',2,2,42,82),(72,'40,41','',1,4,38,83),(73,'','',1,4,39,83),(74,'40,41','',8,4,37,83),(75,'','',2,4,42,NULL),(80,NULL,'Extra spicy',2,1,37,91),(81,NULL,'',1,1,38,91),(82,NULL,'Normal',1,1,37,92),(83,NULL,'Less salt',3,1,39,93),(84,NULL,'Medium spice',1,2,38,94),(85,NULL,'Extra sauce',2,3,39,95);
/*!40000 ALTER TABLE `cart_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_fees`
--

DROP TABLE IF EXISTS `delivery_fees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_fees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `base_fee` decimal(10,2) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `max_distance` decimal(5,2) DEFAULT NULL,
  `min_distance` decimal(5,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_delivery_fee_distance` (`min_distance`,`max_distance`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_fees`
--

LOCK TABLES `delivery_fees` WRITE;
/*!40000 ALTER TABLE `delivery_fees` DISABLE KEYS */;
INSERT INTO `delivery_fees` VALUES (1,11000.00,_binary '',2.00,0.00),(2,18000.00,_binary '',5.00,2.00),(3,25000.00,_binary '',10.00,5.00),(4,35000.00,_binary '',20.00,10.00),(5,45000.00,_binary '',NULL,20.00),(6,8000.00,_binary '',1.00,0.00),(7,12000.00,_binary '',3.00,1.00),(8,20000.00,_binary '',7.00,3.00),(9,30000.00,_binary '',15.00,7.00),(10,40000.00,_binary '',NULL,15.00),(11,13000.00,_binary '',1.50,0.50),(12,28000.00,_binary '',12.00,8.00),(13,50000.00,_binary '',NULL,25.00);
/*!40000 ALTER TABLE `delivery_fees` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food`
--

LOCK TABLES `food` WRITE;
/*!40000 ALTER TABLE `food` DISABLE KEYS */;
INSERT INTO `food` VALUES (37,'abc','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','MAIN','Banh xeo trung','ACTIVE',7,3),(38,'abc','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','MAIN','Banh xeo tôm','ACTIVE',7,3),(39,'abc','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','MAIN','Banh xeo thịt','ACTIVE',7,3),(40,'abc','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ADDITIONAL','Diếp cá','ACTIVE',7,4),(41,'abc','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ADDITIONAL','Xà lách','ACTIVE',7,4),(42,'abc','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','MAIN','Bánh mì vừng đen','ACTIVE',8,1);
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_detail`
--

LOCK TABLES `food_detail` WRITE;
/*!40000 ALTER TABLE `food_detail` DISABLE KEYS */;
INSERT INTO `food_detail` VALUES (13,NULL,6000.00,'2025-04-11 16:19:51.890883',37),(14,NULL,8000.00,'2025-04-11 16:26:39.763991',38),(15,NULL,8000.00,'2025-04-11 16:26:55.893535',39),(16,NULL,2000.00,'2025-04-11 16:27:42.377812',40),(17,NULL,2000.00,'2025-04-11 16:27:56.328044',41),(18,NULL,7000.00,'2025-04-11 16:28:54.670692',42);
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_type`
--

LOCK TABLES `food_type` WRITE;
/*!40000 ALTER TABLE `food_type` DISABLE KEYS */;
INSERT INTO `food_type` VALUES (1,'Bánh mì'),(2,'Bánh xèo'),(3,'Phở'),(4,'Rau');
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
  `type` enum('NEW_ORDER','NEW_ORDER_TO_SHIPPING','ORDER_STATUS_CHANGED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'Bạn có đơn hàng mới cần xác nhận. Vui lòng kiểm tra và phản hồi trong 5 phút.','2025-04-18 10:30:00.000000','Đơn hàng mới','NEW_ORDER'),(2,'Đơn hàng #22 đã chuyển sang trạng thái giao hàng. Hãy lấy hàng tại nhà hàng.','2025-04-18 08:30:00.000000','Trạng thái đơn hàng','ORDER_STATUS_CHANGED'),(3,'Đơn hàng #26 đã sẵn sàng để giao. Shipper hãy đến lấy hàng.','2025-04-18 09:00:00.000000','Sẵn sàng giao hàng','NEW_ORDER_TO_SHIPPING');
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
  KEY `FKlqwjttuisb6oywxdy8sgd46o5` (`order_id`),
  KEY `FKhvelawjcknv4br6qgmindmor2` (`shipper_id`),
  KEY `idx_order_assignment_status` (`status`,`assigned_at`),
  CONSTRAINT `FKhvelawjcknv4br6qgmindmor2` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`),
  CONSTRAINT `FKlqwjttuisb6oywxdy8sgd46o5` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_assignment`
--

LOCK TABLES `order_assignment` WRITE;
/*!40000 ALTER TABLE `order_assignment` DISABLE KEYS */;
INSERT INTO `order_assignment` VALUES (30,'2025-05-31 00:50:42.000000',NULL,NULL,NULL,NULL,'ASSIGNED',91,1),(31,'2025-05-31 00:47:42.000000',NULL,NULL,NULL,NULL,'ASSIGNED',92,1),(32,'2025-05-31 00:42:42.000000',NULL,NULL,NULL,'2025-05-31 00:45:42.000000','ACCEPTED',93,1),(33,'2025-05-31 00:37:42.000000',NULL,NULL,NULL,'2025-05-31 00:40:42.000000','ACCEPTED',94,1),(34,'2025-05-31 00:32:42.000000',NULL,NULL,NULL,'2025-05-31 00:35:42.000000','ACCEPTED',95,1);
/*!40000 ALTER TABLE `order_assignment` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = cp850 */ ;
/*!50003 SET character_set_results = cp850 */ ;
/*!50003 SET collation_connection  = cp850_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `sync_order_shipper_on_assignment_insert` AFTER INSERT ON `order_assignment` FOR EACH ROW BEGIN
    IF NEW.status = 'ACCEPTED' THEN
        UPDATE orders 
        SET shipper_id = NEW.shipper_id,
            assigned_at = NEW.assigned_at,
            accepted_at = NEW.responded_at,
            status = CASE 
                WHEN status = 'PENDING' THEN 'PROCESSING'
                ELSE status 
            END
        WHERE id = NEW.order_id;
    END IF;

    IF NEW.status = 'ASSIGNED' THEN
        UPDATE orders 
        SET shipper_id = NEW.shipper_id,
            assigned_at = NEW.assigned_at
        WHERE id = NEW.order_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `sync_order_shipper_on_assignment_update` AFTER UPDATE ON `order_assignment` FOR EACH ROW BEGIN
    IF NEW.status = 'ACCEPTED' THEN
        UPDATE orders 
        SET shipper_id = NEW.shipper_id,
            assigned_at = NEW.assigned_at,
            accepted_at = NEW.responded_at,
            status = CASE 
                WHEN status = 'PENDING' THEN 'PROCESSING'
                ELSE status 
            END
        WHERE id = NEW.order_id;
    END IF;

    IF NEW.status = 'REJECTED' THEN
        UPDATE orders 
        SET shipper_id = NULL,
            assigned_at = NULL,
            accepted_at = NULL
        WHERE id = NEW.order_id AND shipper_id = NEW.shipper_id;
    END IF;

    IF NEW.status = 'ASSIGNED' THEN
        UPDATE orders 
        SET shipper_id = NEW.shipper_id,
            assigned_at = NEW.assigned_at
        WHERE id = NEW.order_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_voucher`
--

LOCK TABLES `order_voucher` WRITE;
/*!40000 ALTER TABLE `order_voucher` DISABLE KEYS */;
INSERT INTO `order_voucher` VALUES (1,'2025-04-15 01:10:27.554662',23,10),(2,'2025-04-15 01:10:27.638192',23,11),(3,'2025-04-15 02:00:02.953247',24,12),(4,'2025-04-15 02:00:02.972200',24,9),(5,'2025-04-15 02:03:03.779834',25,11),(6,'2025-04-15 02:03:03.800740',25,10),(12,'2025-04-17 18:34:47.494172',59,10),(13,'2025-04-17 18:34:47.567270',59,11),(14,'2025-04-17 22:13:12.594523',63,9),(15,'2025-04-17 22:13:12.612895',63,12),(16,'2025-04-18 00:39:08.311460',81,11),(17,'2025-04-20 00:44:28.283984',83,9),(18,'2025-04-20 00:44:28.303911',83,12);
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
  `status` enum('PENDING','CANCELLED','PROCESSING','READY_FOR_PICKUP','SHIPPING','COMPLETED','REJECTED') DEFAULT NULL,
  `total_price` decimal(11,2) NOT NULL,
  `user_id` bigint NOT NULL,
  `discount_order_price` decimal(9,2) NOT NULL,
  `discount_shipping_fee` decimal(9,2) NOT NULL,
  `accepted_at` datetime(6) DEFAULT NULL,
  `assigned_at` datetime(6) DEFAULT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `delivery_latitude` double DEFAULT NULL,
  `delivery_longitude` double DEFAULT NULL,
  `distance` float DEFAULT NULL,
  `estimated_time` int DEFAULT NULL,
  `gems_earned` int DEFAULT NULL,
  `picked_up_at` datetime(6) DEFAULT NULL,
  `tip` bigint DEFAULT NULL,
  `shipper_id` bigint DEFAULT NULL,
  `delivery_distance` decimal(5,2) DEFAULT NULL,
  `payment_method` enum('COD','VNPAY') DEFAULT NULL,
  `shipper_earning` decimal(9,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKel9kyl84ego2otj2accfd8mr7` (`user_id`),
  KEY `FKcw9s4yihuqduodjn391d630i8` (`shipper_id`),
  KEY `idx_orders_status_shipper` (`status`,`shipper_id`),
  CONSTRAINT `FKcw9s4yihuqduodjn391d630i8` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`),
  CONSTRAINT `FKel9kyl84ego2otj2accfd8mr7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-12 08:36:23.282346',25000.00,'CANCELLED',34000.00,2,0.00,0.00,NULL,NULL,NULL,10.7769,106.7009,NULL,NULL,NULL,NULL,NULL,NULL,8.50,'COD',NULL),(2,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-12 08:41:32.519860',25000.00,'CANCELLED',20000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'COD',NULL),(3,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','NOTE OK','2025-04-12 08:44:09.425076',25000.00,'CANCELLED',98000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'COD',NULL),(7,'VIETNAM','Test','2025-04-13 17:59:17.196415',25000.00,'COMPLETED',84000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'COD',NULL),(8,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-13 18:28:16.838475',25000.00,'COMPLETED',110000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'COD',NULL),(11,'Số 123 Nguyễn Huệ, Q.1, TP.HCM','Giao nhanh nhé','2025-04-21 08:30:15.123456',18000.00,'COMPLETED',42000.00,1,0.00,0.00,'2025-05-31 07:00:00.000000','2025-05-31 06:00:00.000000','2025-05-31 10:00:00.000000',10.772,106.7017,8.2,NULL,NULL,'2025-05-31 08:00:00.000000',7000,2,8.20,'COD',13600.00),(12,'456 Lê Lợi, Q.1, TP.HCM','Không đường','2025-04-21 10:45:22.789012',22000.00,'COMPLETED',58000.00,2,3000.00,0.00,'2025-05-31 07:00:00.000000','2025-05-31 06:00:00.000000','2025-05-31 10:00:00.000000',10.7766,106.7001,11.5,NULL,NULL,'2025-05-31 08:00:00.000000',7000,2,11.50,'VNPAY',18700.00),(13,'789 Đồng Khởi, Q.1, TP.HCM','','2025-04-21 12:20:33.456789',15000.00,'COMPLETED',35000.00,3,0.00,2000.00,'2025-05-31 06:30:00.000000','2025-05-31 05:30:00.000000','2025-05-31 09:30:00.000000',10.7799,106.7025,6.5,NULL,NULL,'2025-05-31 07:30:00.000000',0,3,6.50,'COD',12750.00),(14,'321 Pasteur, Q.3, TP.HCM','Ít cay','2025-04-21 14:15:44.567890',20000.00,'COMPLETED',47000.00,4,2000.00,0.00,'2025-05-31 07:00:00.000000','2025-05-31 06:00:00.000000','2025-05-31 10:00:00.000000',10.7658,106.6912,9.8,NULL,NULL,'2025-05-31 08:00:00.000000',3000,2,9.80,'VNPAY',17000.00),(15,'654 Hai Bà Trưng, Q.1, TP.HCM','Gọi trước 5 phút','2025-04-21 16:40:55.678901',19000.00,'COMPLETED',41000.00,1,0.00,1000.00,'2025-05-31 06:30:00.000000','2025-05-31 05:30:00.000000','2025-05-31 09:30:00.000000',10.7759,106.7034,8.9,NULL,NULL,'2025-05-31 07:30:00.000000',8000,3,8.90,'COD',16150.00),(16,'987 Nguyễn Thị Minh Khai, Q.3, TP.HCM','Nhiều rau','2025-04-21 18:25:11.234567',24000.00,'PROCESSING',53000.00,2,1000.00,0.00,NULL,NULL,NULL,10.7745,106.6889,12.3,NULL,NULL,NULL,NULL,NULL,12.30,'VNPAY',NULL),(17,'147 Calmette, Q.1, TP.HCM','','2025-04-21 19:50:22.345678',17000.00,'PENDING',38000.00,3,0.00,0.00,NULL,NULL,NULL,10.7692,106.6988,7.6,NULL,NULL,NULL,NULL,NULL,7.60,'COD',NULL),(18,'258 Võ Văn Tần, Q.3, TP.HCM','Thêm tương ớt','2025-04-22 09:10:33.456789',21000.00,'PENDING',49000.00,4,0.00,0.00,NULL,NULL,NULL,10.7812,106.6934,10.2,NULL,NULL,NULL,NULL,NULL,10.20,'COD',NULL),(19,'369 Đinh Tiên Hoàng, Q.1, TP.HCM','Giao tận phòng','2025-04-22 11:35:44.567890',16000.00,'PROCESSING',34000.00,1,4000.00,3000.00,NULL,NULL,NULL,10.7701,106.7045,5.8,NULL,NULL,NULL,NULL,NULL,5.80,'COD',NULL),(20,'741 Bùi Thị Xuân, Q.1, TP.HCM','Không hành lá','2025-04-22 13:20:55.678901',23000.00,'PENDING',56000.00,2,0.00,0.00,NULL,NULL,NULL,10.7756,106.6978,11.2,NULL,NULL,NULL,NULL,NULL,11.20,'VNPAY',NULL),(23,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-15 01:10:27.336614',25000.00,'PROCESSING',80000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,10.785,106.705,10,NULL,NULL,NULL,NULL,1,10.00,'COD',NULL),(24,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-15 02:00:02.899393',10000.00,'PROCESSING',129000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,10.785,106.705,10,NULL,NULL,NULL,NULL,1,10.00,'COD',NULL),(28,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:11:30.863127',25000.00,'PROCESSING',108000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(29,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:13:50.214618',25000.00,'PROCESSING',12000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(30,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:17:29.168497',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(31,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:18:26.938774',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(32,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:20:58.841945',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(33,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:24:47.208769',25000.00,'READY_FOR_PICKUP',64000.00,2,0.00,0.00,'2025-05-31 08:15:00.000000','2025-05-31 07:30:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(34,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:26:21.574060',25000.00,'READY_FOR_PICKUP',16000.00,2,0.00,0.00,'2025-05-31 08:15:00.000000','2025-05-31 07:30:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(35,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:29:17.720693',25000.00,'READY_FOR_PICKUP',8000.00,2,0.00,0.00,'2025-05-31 08:15:00.000000','2025-05-31 07:30:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'VNPAY',NULL),(36,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:29:42.042663',25000.00,'SHIPPING',8000.00,2,0.00,0.00,'2025-05-31 08:00:00.000000','2025-05-31 07:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-31 09:00:00.000000',NULL,1,NULL,'VNPAY',NULL),(37,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:29:57.121926',25000.00,'SHIPPING',10000.00,2,0.00,0.00,'2025-05-31 08:00:00.000000','2025-05-31 07:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-31 09:00:00.000000',NULL,1,NULL,'VNPAY',NULL),(38,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:31:30.663548',25000.00,'SHIPPING',8000.00,2,0.00,0.00,'2025-05-31 08:00:00.000000','2025-05-31 07:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-31 09:00:00.000000',NULL,1,NULL,'VNPAY',NULL),(39,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:40:04.721001',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 08:30:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'VNPAY',NULL),(40,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:54:00.158205',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 08:30:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'VNPAY',NULL),(41,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 01:54:58.102680',25000.00,'PROCESSING',16000.00,2,0.00,0.00,NULL,'2025-05-31 08:30:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'VNPAY',NULL),(43,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:05:06.934570',25000.00,'READY_FOR_PICKUP',12000.00,2,0.00,0.00,'2025-05-31 08:30:00.000000','2025-05-31 07:45:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'VNPAY',NULL),(44,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:05:34.204662',25000.00,'READY_FOR_PICKUP',12000.00,2,0.00,0.00,'2025-05-31 08:30:00.000000','2025-05-31 07:45:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'VNPAY',NULL),(45,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:07:17.614060',25000.00,'READY_FOR_PICKUP',8000.00,2,0.00,0.00,'2025-05-31 08:30:00.000000','2025-05-31 07:45:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'VNPAY',NULL),(46,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:07:33.849261',25000.00,'SHIPPING',8000.00,2,0.00,0.00,'2025-05-31 08:15:00.000000','2025-05-31 07:15:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-31 09:15:00.000000',NULL,2,NULL,'VNPAY',NULL),(47,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:07:49.124522',25000.00,'SHIPPING',8000.00,2,0.00,0.00,'2025-05-31 08:15:00.000000','2025-05-31 07:15:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-31 09:15:00.000000',NULL,2,NULL,'VNPAY',NULL),(48,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:08:00.935418',25000.00,'SHIPPING',8000.00,2,0.00,0.00,'2025-05-31 08:15:00.000000','2025-05-31 07:15:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,'2025-05-31 09:15:00.000000',NULL,2,NULL,'VNPAY',NULL),(49,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:08:14.069384',25000.00,'PROCESSING',6000.00,2,0.00,0.00,NULL,'2025-05-31 09:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,'VNPAY',NULL),(50,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:08:53.860731',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 09:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,'VNPAY',NULL),(51,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:09:08.056348',25000.00,'PROCESSING',8000.00,2,0.00,0.00,NULL,'2025-05-31 09:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,'VNPAY',NULL),(52,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 02:09:25.986556',25000.00,'READY_FOR_PICKUP',8000.00,2,0.00,0.00,'2025-05-31 09:00:00.000000','2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,'VNPAY',NULL),(57,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 17:03:30.074877',25000.00,'READY_FOR_PICKUP',16000.00,2,0.00,0.00,'2025-05-31 09:00:00.000000','2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,'VNPAY',NULL),(59,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 18:34:47.449215',15000.00,'READY_FOR_PICKUP',110000.00,2,0.00,0.00,'2025-05-31 09:00:00.000000','2025-05-31 08:00:00.000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,'VNPAY',NULL),(60,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 18:39:31.104727',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(61,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 22:04:01.485746',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(62,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 22:09:13.564844',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(63,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-17 22:13:12.583925',10000.00,'PENDING',105000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(70,'VIETNAM','Test','2025-04-17 23:41:34.174423',25000.00,'PENDING',22000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(74,'VIETNAM','Test','2025-04-18 00:04:07.179244',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(75,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:10:52.142544',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(76,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:20:23.962244',25000.00,'PENDING',16000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(77,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:23:18.142211',25000.00,'PENDING',48000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(78,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:25:00.423851',25000.00,'PENDING',8000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(79,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:25:30.197508',25000.00,'PENDING',12000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(81,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-18 00:39:08.266991',25000.00,'REJECTED',62000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(82,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-19 07:33:52.786243',25000.00,'REJECTED',14000.00,2,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(83,'97 Man Thiện, P.Hiệp Phú, Tp.Thu Đức, Hồ Chí Minh, 7','','2025-04-20 00:44:28.270585',10000.00,'REJECTED',85000.00,4,0.00,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(84,'Test Address for Shipper API','API Test Order','2025-05-31 00:32:02.000000',15000.00,'PENDING',50000.00,1,0.00,0.00,NULL,NULL,NULL,10.7769,106.7009,5.2,NULL,NULL,NULL,NULL,NULL,NULL,'COD',NULL),(85,'Another Test Address','Second API Test','2025-05-31 00:32:02.000000',18000.00,'PENDING',35000.00,2,0.00,0.00,NULL,NULL,NULL,10.7829,106.6959,8.1,NULL,NULL,NULL,NULL,NULL,NULL,'VNPAY',NULL),(91,'Test Order 1 - PENDING Status','Test note 1','2025-05-31 00:50:42.000000',15000.00,'COMPLETED',50000.00,1,0.00,0.00,NULL,'2025-05-31 00:50:42.000000','2025-05-31 10:00:00.000000',10.7769,106.7009,5.2,NULL,NULL,NULL,5000,1,NULL,'COD',12750.00),(92,'Test Order 2 - PENDING Status','Test note 2','2025-05-31 00:45:42.000000',18000.00,'COMPLETED',65000.00,1,0.00,0.00,NULL,'2025-05-31 00:47:42.000000','2025-05-31 10:00:00.000000',10.7829,106.6959,8.1,NULL,NULL,NULL,0,1,NULL,'VNPAY',15300.00),(93,'Test Order 3 - PROCESSING Status','Test note 3','2025-05-31 00:40:42.000000',20000.00,'COMPLETED',45000.00,2,0.00,0.00,'2025-05-31 00:45:42.000000','2025-05-31 00:42:42.000000','2025-05-31 10:00:00.000000',10.78,106.7,6.5,NULL,NULL,NULL,0,1,NULL,'COD',17000.00),(94,'Test Order 4 - READY_FOR_PICKUP Status','Test note 4','2025-05-31 00:35:42.000000',22000.00,'COMPLETED',75000.00,2,0.00,0.00,'2025-05-31 00:40:42.000000','2025-05-31 00:37:42.000000','2025-05-31 10:00:00.000000',10.785,106.705,7.2,NULL,NULL,NULL,10000,1,NULL,'VNPAY',18700.00),(95,'Test Order 5 - SHIPPING Status','Test note 5','2025-05-31 00:30:42.000000',25000.00,'COMPLETED',85000.00,3,0.00,0.00,'2025-05-31 00:35:42.000000','2025-05-31 00:32:42.000000','2025-05-31 10:00:00.000000',10.79,106.71,9.1,NULL,NULL,NULL,0,1,NULL,'COD',21250.00),(96,'Test Address A','Test A','2025-05-31 08:00:00.000000',15000.00,'COMPLETED',50000.00,1,0.00,0.00,NULL,NULL,'2025-05-31 10:00:00.000000',10.77,106.7,5,NULL,NULL,NULL,NULL,1,NULL,'COD',NULL),(97,'Test Address B','Test B','2025-05-31 08:30:00.000000',18000.00,'COMPLETED',45000.00,2,0.00,0.00,NULL,NULL,'2025-05-31 10:30:00.000000',10.78,106.71,6,NULL,NULL,NULL,NULL,1,NULL,'COD',NULL),(98,'Test Address C','Test C','2025-05-31 09:00:00.000000',20000.00,'COMPLETED',55000.00,3,0.00,0.00,NULL,NULL,'2025-05-31 11:00:00.000000',10.79,106.72,7,NULL,NULL,NULL,NULL,1,NULL,'COD',NULL),(99,'Test Address D','Test D','2025-05-31 09:30:00.000000',22000.00,'COMPLETED',42000.00,1,0.00,0.00,NULL,NULL,'2025-05-31 11:30:00.000000',10.8,106.73,8,NULL,NULL,NULL,NULL,1,NULL,'COD',NULL),(100,'Test Address E','Test E','2025-05-31 10:00:00.000000',25000.00,'COMPLETED',48000.00,2,0.00,0.00,NULL,NULL,'2025-05-31 12:00:00.000000',10.81,106.74,9,NULL,NULL,NULL,NULL,1,NULL,'COD',NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
INSERT INTO `payment_info` VALUES (1,'2025-04-17 22:09:57.874128',33000.00,'1331db78-d508-4bd6-9030-766b220c1409','VNPAY','SUCCESS',62),(2,'2025-04-17 22:15:37.375063',115000.00,'4cb74a06-7f7a-41dc-b2e5-b0b9188fa9cb','VNPAY','SUCCESS',63),(8,'2025-04-17 23:41:34.220977',47000.00,'1607c88c-a130-4d44-816b-b2f8927607b2','VNPAY','SUCCESS',70),(12,'2025-04-18 00:04:33.429226',33000.00,'6944ed91-e211-41fd-a204-52b5250fe774','VNPAY','SUCCESS',74),(13,'2025-04-18 00:10:52.161009',41000.00,'09730e32-b98b-4a05-874e-e3f5e2927a4c','VNPAY','SUCCESS',75),(14,'2025-04-18 00:20:23.982190',41000.00,'08ab22e6-9769-4cfb-af9a-c421aa0c5e9e','VNPAY','SUCCESS',76),(15,'2025-04-18 00:23:18.145041',73000.00,'2789db3e-01d5-45f7-91bb-7a32dc4dc5bd','VNPAY','SUCCESS',77),(16,'2025-04-18 00:25:00.428372',33000.00,'e3437bda-8d07-43c8-acce-062687ca1862','VNPAY','SUCCESS',78),(17,'2025-04-18 00:27:13.995393',37000.00,'4548ccc7-98bf-4890-9a9a-aab883a60cb1','VNPAY','SUCCESS',79),(18,'2025-04-18 00:40:35.806121',87000.00,'90072e1c-bc62-4681-a3d3-9d770c40f237','VNPAY','SUCCESS',81),(19,'2025-04-20 00:44:28.311967',95000.00,'ed406652-6621-4052-88a6-9cc42926ffdf','VNPAY','SUCCESS',83);
/*!40000 ALTER TABLE `payment_info` ENABLE KEYS */;
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
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `account_id` bigint NOT NULL,
  `address_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK979xvypjc2lwr1ia4kq77cko0` (`email`),
  UNIQUE KEY `UKn219pfkvmxn9tydrxyr0xq5br` (`account_id`),
  UNIQUE KEY `UK2b01rrbfd5g6hklh8ei57uhgn` (`address_id`),
  CONSTRAINT `FK96q13p1ptpewvus590a8o83xt` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FKpt08lhjedg4gagsyk99axqnhh` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant`
--

LOCK TABLES `restaurant` WRITE;
/*!40000 ALTER TABLE `restaurant` DISABLE KEYS */;
INSERT INTO `restaurant` VALUES (7,'23:30:00.000000','2025-04-11','Modern taste, classic roots','res6@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','Urban Flavor','11:00:00.000000','012345636','ACTIVE',3,1),(8,'23:30:00.000000','2025-04-11','Modern taste, classic roots','res1@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery','11:00:00.000000','012345637','ACTIVE',4,2),(50,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res2@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery6','11:00:00.000000','012345632','ACTIVE',41,3),(51,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res3@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery2','11:00:00.000000','012345633','ACTIVE',42,5),(52,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res4@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery7','11:00:00.000000','012345634','ACTIVE',43,6),(53,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res5@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery11','11:00:00.000000','012345635','ACTIVE',44,7),(54,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res7@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery 3','11:00:00.000000','012345636','ACTIVE',45,9),(55,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res8@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery12','11:00:00.000000','012345637','ACTIVE',46,10),(56,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res9@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery14','11:00:00.000000','012345639','ACTIVE',47,11),(57,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res10@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery4','11:00:00.000000','012345640','ACTIVE',48,12),(58,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res11@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery15','11:00:00.000000','012345641','ACTIVE',49,13),(59,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res12@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery 5','11:00:00.000000','012345642','ACTIVE',50,14),(60,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res13@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery16','11:00:00.000000','012345643','ACTIVE',51,15),(61,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res14@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery10','11:00:00.000000','012345644','ACTIVE',52,16),(62,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res15@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery8','11:00:00.000000','012345645','ACTIVE',53,17),(63,'23:30:00.000000','2025-04-18','Modern taste, classic roots','res16@gmail.com','https://th.bing.com/th/id/OIP.QwZZ3QVzEOUZNPdEuUZMwwHaE8?w=256&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7','ABC Bakery13','11:00:00.000000','012345638','ACTIVE',54,18);
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
  `order_string` varchar(255) NOT NULL,
  `rating` decimal(38,2) NOT NULL,
  `reply_message` varchar(255) DEFAULT NULL,
  `reply_time` datetime(6) DEFAULT NULL,
  `review_message` varchar(255) DEFAULT NULL,
  `order_id` bigint NOT NULL,
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
  `description` varchar(1000) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `peak_end_time` time(6) DEFAULT NULL,
  `peak_start_time` time(6) DEFAULT NULL,
  `required_deliveries` int DEFAULT NULL,
  `type` enum('DAILY','PEAK_HOUR','BONUS','ACHIEVEMENT') DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `gems_value` int DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  `required_distance` float DEFAULT NULL,
  `required_orders` int DEFAULT NULL,
  `required_rating` float DEFAULT NULL,
  `reward_value` decimal(10,2) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','EXPIRED','INACTIVE') NOT NULL,
  `title` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rewards`
--

LOCK TABLES `rewards` WRITE;
/*!40000 ALTER TABLE `rewards` DISABLE KEYS */;
INSERT INTO `rewards` VALUES (1,50000.00,'Hoàn thành 10 đơn hàng trong ngày để nhận thưởng',_binary '','Daily Champion',NULL,NULL,10,'DAILY','2025-01-01','2025-12-31','admin','2025-12-31 23:59:59.000000',20,'https://cdn-icons-png.flaticon.com/512/2583/2583441.png',NULL,10,4.5,50000.00,'2025-01-01 00:00:00.000000','ACTIVE','Nhà vô địch hàng ngày'),(2,100000.00,'Giao hàng trong giờ cao điểm (11:00-13:00 & 17:00-19:00)',_binary '','Peak Hour Warrior','19:00:00.000000','11:00:00.000000',5,'PEAK_HOUR','2025-01-01','2025-12-31','admin','2025-12-31 23:59:59.000000',35,'https://cdn-icons-png.flaticon.com/512/1055/1055666.png',NULL,5,4,100000.00,'2025-01-01 00:00:00.000000','ACTIVE','Chiến binh giờ vàng'),(3,500000.00,'Hoàn thành 100 đơn hàng thành công',_binary '','Century Milestone',NULL,NULL,NULL,'ACHIEVEMENT','2025-01-01','2025-12-31','admin','2025-12-31 23:59:59.000000',100,'https://cdn-icons-png.flaticon.com/512/2040/2040946.png',NULL,100,NULL,500000.00,'2025-01-01 00:00:00.000000','ACTIVE','Cột mốc thế kỷ'),(4,200000.00,'Thưởng đặc biệt cuối tuần',_binary '','Weekend Bonus',NULL,NULL,15,'BONUS','2025-01-01','2025-12-31','admin','2025-12-31 23:59:59.000000',50,'https://cdn-icons-png.flaticon.com/512/2936/2936644.png',NULL,15,4.3,200000.00,'2025-01-01 00:00:00.000000','ACTIVE','Phần thưởng cuối tuần'),(5,1000000.00,'Shipper xuất sắc của tháng',_binary '','Monthly Star',NULL,NULL,200,'BONUS','2025-01-01','2025-12-31','admin','2025-12-31 23:59:59.000000',200,'https://cdn-icons-png.flaticon.com/512/1828/1828884.png',NULL,200,4.8,1000000.00,'2025-01-01 00:00:00.000000','ACTIVE','Ngôi sao tháng');
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `cancellation_rate` float NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `current_latitude` double DEFAULT NULL,
  `current_longitude` double DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gems` int NOT NULL,
  `is_online` bit(1) NOT NULL,
  `modified_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `rating` decimal(3,2) NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') DEFAULT NULL,
  `total_orders` int NOT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `account_id` bigint DEFAULT NULL,
  `completed_orders` int NOT NULL,
  `license_plate` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKndrldc7jwnhuss6rxr0gjg6gm` (`email`),
  UNIQUE KEY `UKlj4yjb586j4658glfwa54i120` (`phone`),
  UNIQUE KEY `UK2uxcy232tkd6p5mtk4530yn0u` (`account_id`),
  KEY `idx_shipper_location` (`current_latitude`,`current_longitude`,`is_online`,`status`),
  KEY `idx_shipper_online_status` (`status`,`is_online`),
  CONSTRAINT `FKckouiteoyj90usbwdw9b3jpg5` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipper`
--

LOCK TABLES `shipper` WRITE;
/*!40000 ALTER TABLE `shipper` DISABLE KEYS */;
INSERT INTO `shipper` VALUES (1,95.5,2.1,'2024-01-15 08:30:00.000000',11,110,'marcusgrabfood@gmail.com',150,_binary '','2025-05-30 11:21:30.070995','Marcus Grabfood','0111111111',4.85,'ACTIVE',10,'Man Dan','Xe máy',11,10,'29A-12345'),(2,89.2,5.8,'2024-02-10 09:15:00.000000',10.7829,106.6959,'tran.thi.b@gmail.com',89,_binary '','2025-05-30 07:45:00.000000','Trần Thị B','0222222222',4.72,'ACTIVE',178,'BIKE002','Xe máy',12,165,'29B-67890'),(3,92.8,3.2,'2024-01-20 10:00:00.000000',21.0285,105.8542,'le.minh.c@gmail.com',120,_binary '\0','2025-05-30 06:30:00.000000','Lê Minh C','0333333333',4.90,'ACTIVE',312,'BIKE003','Xe máy',13,298,'30A-11111'),(4,87.5,7.2,'2024-03-05 11:30:00.000000',16.0471,108.2068,'pham.duc.d@gmail.com',75,_binary '','2025-05-30 09:15:00.000000','Phạm Đức D','0444444444',4.65,'ACTIVE',156,'BIKE004','Xe máy',14,142,'43A-22222'),(5,94.1,2.9,'2024-01-30 12:45:00.000000',10.8063,106.7161,'hoang.thi.e@gmail.com',200,_binary '','2025-05-30 08:30:00.000000','Hoàng Thị E','0555555555\n',4.88,'ACTIVE',289,'BIKE005','Xe máy',15,278,'29C-33333'),(6,91.3,4.5,'2024-02-15 13:20:00.000000',10.758,106.6621,'vu.van.f@gmail.com',110,_binary '\0','2025-05-30 07:00:00.000000','Vũ Văn F','0666666666',4.75,'ACTIVE',203,'BIKE006','Xe máy',16,188,'29D-44444'),(7,88.7,6.1,'2024-03-10 14:10:00.000000',21.0097,105.8478,'dao.thi.g@gmail.com',95,_binary '','2025-05-30 09:45:00.000000','Đào Thị G','0777777777',4.70,'ACTIVE',167,'BIKE007','Xe máy',17,152,'30B-55555'),(8,96.2,1.8,'2024-02-20 15:00:00.000000',16.0544,108.2022,'bui.minh.h@gmail.com',180,_binary '','2025-05-30 08:15:00.000000','Bùi Minh H','0888888888',4.92,'ACTIVE',334,'BIKE008','Xe máy',18,325,'43B-66666'),(9,90.4,4.8,'2024-01-25 16:30:00.000000',10.7859,106.7019,'ngo.van.i@gmail.com',65,_binary '\0','2025-05-30 06:45:00.000000','Ngô Văn I','0999999999',4.68,'ACTIVE',145,'BIKE009','Xe máy',19,135,'29E-77777'),(10,93.6,3.1,'2024-03-01 17:15:00.000000',10.8159,106.628,'ly.thi.k@gmail.com',135,_binary '','2025-05-30 09:00:00.000000','Lý Thị K','0000000000',4.82,'ACTIVE',256,'BIKE010','Xe máy',20,243,'29F-88888');
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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipper_rewards`
--

LOCK TABLES `shipper_rewards` WRITE;
/*!40000 ALTER TABLE `shipper_rewards` DISABLE KEYS */;
INSERT INTO `shipper_rewards` VALUES (4,NULL,60,'Cần thêm 2 đơn nữa',3,'ELIGIBLE',2,2),(5,NULL,85,'Sắp đạt mục tiêu',85,'ELIGIBLE',3,3),(6,'2025-05-25 20:00:00.000000',100,'Weekend warrior',15,'CLAIMED',4,4),(7,NULL,40,'Cần cố gắng thêm',4,'ELIGIBLE',1,4),(8,'2025-05-30 14:20:00.000000',100,'Xuất sắc trong giờ cao điểm',6,'CLAIMED',2,5),(9,NULL,90,'Chỉ còn 1 đơn nữa',9,'ELIGIBLE',1,6),(10,NULL,75,'Tiến triển ổn định',75,'ELIGIBLE',3,6),(11,'2025-05-27 16:45:00.000000',100,'Hoàn thành weekend bonus',18,'CLAIMED',4,6),(12,NULL,50,'Đang nỗ lực',5,'ELIGIBLE',1,7),(13,NULL,20,'Mới bắt đầu',2,'ELIGIBLE',2,8),(14,NULL,95,'Gần đạt milestone',95,'ELIGIBLE',3,9),(15,'2025-05-26 11:30:00.000000',100,'Champion của ngày',12,'CLAIMED',1,10),(17,NULL,30,'Cần cải thiện',3,'ELIGIBLE',2,10),(18,'2025-05-24 19:15:00.000000',100,'Excellent performance',20,'CLAIMED',4,10),(19,NULL,70,'Đang trên đà tốt',70,'ELIGIBLE',3,10),(21,'2025-05-31 06:37:46.627765',100,NULL,NULL,'CLAIMED',2,1);
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
  `order_id` bigint DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','PENDING') NOT NULL,
  `tip` bigint DEFAULT NULL,
  `transaction_date` datetime(6) NOT NULL,
  `type` enum('BONUS','COD_DEPOSIT','COMMISSION','EARNING','TIP','TOP_UP') NOT NULL,
  `shipper_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ajawi9yo3w0xwwm329irurji` (`shipper_id`),
  CONSTRAINT `FK3ajawi9yo3w0xwwm329irurji` FOREIGN KEY (`shipper_id`) REFERENCES `shipper` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (9,100000,NULL,NULL,'Withdrawal to Vietcombank - 1234567890',NULL,NULL,'PENDING',NULL,'2025-05-31 07:17:27.105601','TOP_UP',1),(10,15000,2250,15000,'Delivery earning for Order #91',12750,91,'COMPLETED',0,'2025-05-31 00:55:42.000000','EARNING',1),(11,18000,2700,18000,'Delivery earning for Order #92',15300,92,'COMPLETED',0,'2025-05-31 00:50:42.000000','EARNING',1),(12,20000,3000,20000,'Delivery earning for Order #93',17000,93,'COMPLETED',0,'2025-05-31 00:48:42.000000','EARNING',1),(13,22000,3300,22000,'Delivery earning for Order #94',18700,94,'COMPLETED',0,'2025-05-31 00:43:42.000000','EARNING',1),(14,25000,3750,25000,'Delivery earning for Order #95',21250,95,'COMPLETED',0,'2025-05-31 00:38:42.000000','EARNING',1),(15,5000,0,0,'Customer tip for excellent service',5000,91,'COMPLETED',5000,'2025-05-31 01:00:42.000000','TIP',1),(16,10000,0,0,'Customer tip for fast delivery',10000,94,'COMPLETED',10000,'2025-05-31 00:48:42.000000','TIP',1),(17,2250,2250,0,'Platform commission 15% for Order #91',-2250,91,'COMPLETED',0,'2025-05-31 00:55:42.000000','COMMISSION',1),(18,2700,2700,0,'Platform commission 15% for Order #92',-2700,92,'COMPLETED',0,'2025-05-31 00:50:42.000000','COMMISSION',1),(19,3000,3000,0,'Platform commission 15% for Order #93',-3000,93,'COMPLETED',0,'2025-05-31 00:48:42.000000','COMMISSION',1),(20,50000,0,0,'COD collection for Order #91',50000,91,'COMPLETED',0,'2025-05-31 00:56:42.000000','COD_DEPOSIT',1),(21,45000,0,0,'COD collection for Order #93',45000,93,'COMPLETED',0,'2025-05-31 00:49:42.000000','COD_DEPOSIT',1),(22,85000,0,0,'COD collection for Order #95',85000,95,'COMPLETED',0,'2025-05-31 00:39:42.000000','COD_DEPOSIT',1),(23,50000,0,0,'Daily completion bonus - 5 orders',50000,NULL,'COMPLETED',0,'2025-05-31 01:05:42.000000','BONUS',1),(24,25000,0,0,'Peak hour bonus',25000,NULL,'COMPLETED',0,'2025-05-30 12:30:42.000000','BONUS',1),(25,200000,0,0,'Withdrawal to Vietcombank - 1234567890',-200000,NULL,'COMPLETED',0,'2025-05-30 15:20:42.000000','TOP_UP',1),(26,150000,0,0,'Withdrawal to BIDV - 9876543210',-150000,NULL,'PENDING',0,'2025-05-31 09:15:42.000000','TOP_UP',1),(27,16000,2400,16000,'Delivery earning for Order #11',13600,11,'COMPLETED',0,'2025-05-30 09:00:42.000000','EARNING',2),(28,22000,3300,22000,'Delivery earning for Order #12',18700,12,'COMPLETED',0,'2025-05-30 11:15:42.000000','EARNING',2),(29,20000,3000,20000,'Delivery earning for Order #14',17000,14,'COMPLETED',0,'2025-05-30 14:45:42.000000','EARNING',2),(30,7000,0,0,'Customer tip for good service',7000,12,'COMPLETED',7000,'2025-05-30 11:20:42.000000','TIP',2),(31,3000,0,0,'Customer tip',3000,14,'COMPLETED',3000,'2025-05-30 14:50:42.000000','TIP',2),(32,2400,2400,0,'Platform commission 15% for Order #11',-2400,11,'COMPLETED',0,'2025-05-30 09:00:42.000000','COMMISSION',2),(33,3300,3300,0,'Platform commission 15% for Order #12',-3300,12,'COMPLETED',0,'2025-05-30 11:15:42.000000','COMMISSION',2),(34,42000,0,0,'COD collection for Order #11',42000,11,'COMPLETED',0,'2025-05-30 09:05:42.000000','COD_DEPOSIT',2),(35,58000,0,0,'COD collection for Order #12',58000,12,'COMPLETED',0,'2025-05-30 11:18:42.000000','COD_DEPOSIT',2),(36,30000,0,0,'Weekly completion bonus',30000,NULL,'COMPLETED',0,'2025-05-30 18:00:42.000000','BONUS',2),(37,100000,0,0,'Failed withdrawal - insufficient balance',0,NULL,'FAILED',0,'2025-05-30 16:30:42.000000','TOP_UP',2),(38,15000,2250,15000,'Delivery earning for Order #13',12750,13,'COMPLETED',0,'2025-05-30 12:45:42.000000','EARNING',3),(39,19000,2850,19000,'Delivery earning for Order #15',16150,15,'COMPLETED',0,'2025-05-30 17:05:42.000000','EARNING',3),(40,17000,2550,17000,'Delivery earning for Order #17',14450,17,'COMPLETED',0,'2025-05-30 20:15:42.000000','EARNING',3),(41,8000,0,0,'Customer tip for fast delivery',8000,15,'COMPLETED',8000,'2025-05-30 17:08:42.000000','TIP',3),(42,5000,0,0,'Customer tip',5000,17,'COMPLETED',5000,'2025-05-30 20:18:42.000000','TIP',3),(43,2250,2250,0,'Platform commission 15%',-2250,13,'COMPLETED',0,'2025-05-30 12:45:42.000000','COMMISSION',3),(44,2850,2850,0,'Platform commission 15%',-2850,15,'COMPLETED',0,'2025-05-30 17:05:42.000000','COMMISSION',3),(45,35000,0,0,'COD collection for Order #13',35000,13,'COMPLETED',0,'2025-05-30 12:48:42.000000','COD_DEPOSIT',3),(46,41000,0,0,'COD collection for Order #15',41000,15,'COMPLETED',0,'2025-05-30 17:10:42.000000','COD_DEPOSIT',3),(47,75000,0,0,'Achievement bonus - 100 orders milestone',75000,NULL,'COMPLETED',0,'2025-05-30 20:30:42.000000','BONUS',3),(48,21000,3150,21000,'Delivery earning for Order #18',17850,18,'COMPLETED',0,'2025-05-31 09:30:42.000000','EARNING',4),(49,23000,3450,23000,'Delivery earning for Order #20',19550,20,'COMPLETED',0,'2025-05-31 13:45:42.000000','EARNING',4),(50,12000,0,0,'Customer tip - processing',12000,18,'PENDING',12000,'2025-05-31 09:35:42.000000','TIP',4),(51,3150,3150,0,'Platform commission 15%',-3150,18,'COMPLETED',0,'2025-05-31 09:30:42.000000','COMMISSION',4),(52,49000,0,0,'COD collection - pending verification',49000,18,'PENDING',0,'2025-05-31 09:38:42.000000','COD_DEPOSIT',4),(53,80000,0,0,'Withdrawal request - under review',-80000,NULL,'PENDING',0,'2025-05-31 14:20:42.000000','TOP_UP',4),(54,24000,3600,24000,'Delivery earning for Order #16',20400,16,'COMPLETED',0,'2025-05-30 18:50:42.000000','EARNING',5),(55,16000,2400,16000,'Delivery earning for Order #19',13600,19,'COMPLETED',0,'2025-05-31 11:55:42.000000','EARNING',5),(56,15000,0,0,'Generous customer tip',15000,16,'COMPLETED',15000,'2025-05-30 18:55:42.000000','TIP',5),(57,3600,3600,0,'Platform commission 15%',-3600,16,'COMPLETED',0,'2025-05-30 18:50:42.000000','COMMISSION',5),(58,53000,0,0,'COD collection for Order #16',53000,16,'COMPLETED',0,'2025-05-30 18:52:42.000000','COD_DEPOSIT',5),(59,34000,0,0,'COD collection for Order #19',34000,19,'COMPLETED',0,'2025-05-31 11:58:42.000000','COD_DEPOSIT',5),(60,40000,0,0,'Peak hour bonus',40000,NULL,'COMPLETED',0,'2025-05-30 19:00:42.000000','BONUS',5),(61,25000,0,0,'Quality service bonus',25000,NULL,'COMPLETED',0,'2025-05-31 12:00:42.000000','BONUS',5),(62,300000,0,0,'Withdrawal to ACB Bank - 1122334455',-300000,NULL,'COMPLETED',0,'2025-05-30 20:15:42.000000','TOP_UP',5);
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
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2025-04-11 15:38:21.922756','n21dccn126@student.ptithcm.edu.vn','2025-04-11 15:38:21.922756','Phan Phi Hùng','01111111111',1),(2,'2025-04-12 08:34:24.073561','lhphuc.24110@gmail.com','2025-04-12 08:34:24.073561','Le Hong Phuc','0869738540',5),(3,'2025-04-19 07:44:43.578852','lehongphuc24102003@gmail.com','2025-04-19 07:44:43.578852','Phúc Lê','0577017530',55),(4,'2025-04-20 00:42:58.223579','lhphuc.2410@gmail.com','2025-04-20 00:42:58.223719','Hồng Phúc Lê','0591674848',56),(11,'2025-04-30 00:42:58.223579','nguyen.van.a@gmail.com','2025-05-30 17:25:00.000000','Nguyễn Văn A','0111111111',11),(12,'2025-04-30 00:42:58.223579','tran.thi.b@gmail.com','2025-05-30 17:25:00.000000','Trần Thị B','0222222222',12),(13,'2025-04-30 00:42:58.223579','le.minh.c@gmail.com','2025-05-30 17:25:00.000000','Lê Minh C','0333333333',13),(14,'2025-04-30 00:42:58.223579','pham.duc.d@gmail.com','2025-05-30 17:25:00.000000','Phạm Đức D','0444444444',14),(15,'2025-04-30 00:42:58.223579','hoang.thi.e@gmail.com','2025-05-30 17:25:00.000000','Hoàng Thị E','0555555555',15),(16,'2025-04-30 00:42:58.223579','vu.van.f@gmail.com','2025-05-30 17:25:00.000000','Vũ Văn F','0666666666',16),(17,'2025-04-30 00:42:58.223579','dao.thi.g@gmail.com','2025-05-30 17:25:00.000000','Đào Thị G','0777777777',17),(18,'2025-04-30 00:42:58.223579','bui.minh.h@gmail.com','2025-05-30 17:25:00.000000','Bùi Minh H','0888888888',18),(19,'2025-04-30 00:42:58.223579','ngo.van.i@gmail.com','2025-05-30 17:25:00.000000','Ngô Văn I','0999999999',19),(20,'2025-04-30 00:42:58.223579','ly.thi.k@gmail.com','2025-05-30 17:25:00.000000','Lý Thị K','0000000000',20);
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_notification`
--

LOCK TABLES `user_notification` WRITE;
/*!40000 ALTER TABLE `user_notification` DISABLE KEYS */;
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
INSERT INTO `voucher_detail` VALUES (9,'2025-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,14,10),(10,'2025-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,15,10),(11,'2025-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,16,10),(12,'2025-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,18,10),(13,'2025-04-20 23:30:00.000000','2025-03-23 23:59:59.000000',NULL,19,5);
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wallet`
--

LOCK TABLES `wallet` WRITE;
/*!40000 ALTER TABLE `wallet` DISABLE KEYS */;
INSERT INTO `wallet` VALUES (1,125000,350000,_binary '','2025-05-30 09:30:00.000000',8500000,150000,25600000,1200000,1),(2,89000,320000,_binary '','2025-05-30 09:15:00.000000',6200000,95000,18700000,890000,2),(3,156000,680000,_binary '','2025-05-30 08:45:00.000000',12300000,220000,34500000,1750000,3),(4,67000,180000,_binary '','2025-05-30 10:00:00.000000',4800000,78000,14200000,650000,4),(5,198000,520000,_binary '','2025-05-30 09:45:00.000000',9800000,185000,28900000,1400000,5),(6,112000,290000,_binary '','2025-05-30 08:30:00.000000',7100000,120000,22100000,980000,6),(7,78000,240000,_binary '','2025-05-30 10:15:00.000000',5600000,89000,16800000,720000,7),(8,234000,750000,_binary '','2025-05-30 09:00:00.000000',14200000,267000,38700000,1950000,8),(9,45000,165000,_binary '','2025-05-30 08:15:00.000000',3900000,65000,12300000,560000,9),(10,167000,420000,_binary '','2025-05-30 09:30:00.000000',8900000,167000,26800000,1280000,10);
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

-- Dump completed on 2025-06-02 16:45:01
