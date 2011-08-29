drop database if exists flexbuh;
create database flexbuh DEFAULT CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
use flexbuh;
grant all privileges on flexbuh.* to flexbuh identified by 'flexbuh';
flush privileges;
