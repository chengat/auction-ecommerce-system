-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema auctiondatabase
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema auctiondatabase
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `auctiondatabase` DEFAULT CHARACTER SET utf8mb3 ;
-- -----------------------------------------------------
-- Schema logindatabase
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema logindatabase
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `logindatabase` DEFAULT CHARACTER SET utf8mb3 ;
USE `auctiondatabase` ;

-- -----------------------------------------------------
-- Table `auctiondatabase`.`catalogue`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `auctiondatabase`.`catalogue` (
  `itemId` INT NOT NULL AUTO_INCREMENT,
  `itemName` VARCHAR(100) NOT NULL,
  `itemDesc` VARCHAR(1000) NULL DEFAULT NULL,
  `shippingCost` DOUBLE NULL DEFAULT NULL,
  `expeditedShippingCost` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`itemId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `auctiondatabase`.`auctions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `auctiondatabase`.`auctions` (
  `auctionId` INT NOT NULL AUTO_INCREMENT,
  `itemId` INT NOT NULL,
  `currentBid` INT NOT NULL,
  `auctionType` CHAR(10) NOT NULL,
  `endTime` BIGINT NULL DEFAULT NULL,
  `increment` DOUBLE NULL DEFAULT NULL,
  `reservePrice` DOUBLE NULL DEFAULT NULL,
  `startingPrice` DOUBLE NULL DEFAULT NULL,
  `isClosed` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`auctionId`),
  INDEX `itemId_idx` (`itemId` ASC),
  CONSTRAINT `itemId`
    FOREIGN KEY (`itemId`)
    REFERENCES `auctiondatabase`.`catalogue` (`itemId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `logindatabase`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logindatabase`.`users` (
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nameFirst` VARCHAR(45) NOT NULL,
  `nameLast` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `auctiondatabase`.`bids`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `auctiondatabase`.`bids` (
  `bidId` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `auctionId` INT NOT NULL,
  `bidPrice` DOUBLE NOT NULL,
  `bidTime` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`bidId`),
  INDEX `userId_idx` (`username` ASC),
  INDEX `auctionId_idx` (`auctionId` ASC),
  CONSTRAINT `auctionId`
    FOREIGN KEY (`auctionId`)
    REFERENCES `auctiondatabase`.`auctions` (`auctionId`),
  CONSTRAINT `userId`
    FOREIGN KEY (`username`)
    REFERENCES `logindatabase`.`users` (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `auctiondatabase`.`creditcards`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `auctiondatabase`.`creditcards` (
  `cardId` INT NOT NULL AUTO_INCREMENT,
  `cardNumber` VARCHAR(26) NULL DEFAULT NULL,
  `cardholderName` VARCHAR(100) NOT NULL,
  `cardExpiry` DATE NOT NULL,
  `securityCode` INT NOT NULL,
  `cardType` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`cardId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `auctiondatabase`.`payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `auctiondatabase`.`payments` (
  `auctionId` INT NOT NULL,
  `userId` VARCHAR(255) NOT NULL,
  `amount` DOUBLE NOT NULL,
  `creditCardId` INT NULL DEFAULT NULL,
  `bidId` INT NULL DEFAULT NULL,
  `shippingType` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`auctionId`, `userId`),
  INDEX `username_idx` (`userId` ASC) ,
  INDEX `cardId_idx` (`creditCardId` ASC) ,
  INDEX `fk_bidId` (`bidId` ASC) ,
  CONSTRAINT `cardId`
    FOREIGN KEY (`creditCardId`)
    REFERENCES `auctiondatabase`.`creditcards` (`cardId`),
  CONSTRAINT `fk_bidId`
    FOREIGN KEY (`bidId`)
    REFERENCES `auctiondatabase`.`bids` (`bidId`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `paymentAuctionId`
    FOREIGN KEY (`auctionId`)
    REFERENCES `auctiondatabase`.`auctions` (`auctionId`)
    ON UPDATE RESTRICT,
  CONSTRAINT `paymentUsername`
    FOREIGN KEY (`userId`)
    REFERENCES `logindatabase`.`users` (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

USE `logindatabase` ;

-- -----------------------------------------------------
-- Table `logindatabase`.`addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logindatabase`.`addresses` (
  `username` VARCHAR(255) NOT NULL,
  `streetName` VARCHAR(100) NOT NULL,
  `streetNumber` INT NOT NULL,
  `postalCode` CHAR(6) NOT NULL,
  `city` VARCHAR(60) NOT NULL,
  `country` VARCHAR(60) NOT NULL,
  PRIMARY KEY (`username`),
  CONSTRAINT `addressusername`
    FOREIGN KEY (`username`)
    REFERENCES `logindatabase`.`users` (`username`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `logindatabase`.`tokens`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `logindatabase`.`tokens` (
  `username` VARCHAR(255) NOT NULL,
  `token` VARCHAR(255) NOT NULL,
  `socketConnection` BLOB NULL DEFAULT NULL,
  `selectedAuction` VARCHAR(45) NULL DEFAULT NULL,
  `provisionedAt` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`token`),
  INDEX `username_idx` (`username` ASC),
  CONSTRAINT `username`
    FOREIGN KEY (`username`)
    REFERENCES `logindatabase`.`users` (`username`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
