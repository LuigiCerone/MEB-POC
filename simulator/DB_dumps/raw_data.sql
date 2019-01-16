drop database raw_data;
create database raw_data;
use raw_data;

create table analytics
(
  id       int auto_increment
    primary key,

  equipId    varchar(30)          not null,
  equipName    varchar(30)          not null,

  recipeId   varchar(30)          not null,
  recipeName   varchar(30)          not null,

  stepId     varchar(30)          null,
  stepName     varchar(30)          null,

  fakeData mediumtext not null
);

