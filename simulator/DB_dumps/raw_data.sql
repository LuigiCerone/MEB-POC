drop database raw_data;
create database raw_data;
use raw_data;

create table analytics
(
  id              int auto_increment
    primary key,

  oid             varchar(30) not null,
  nameTranslation varchar(30) not null,
  type            int         not null, # 0 means equip, 1 means recipe, 2 means step translation.

  fakeData        mediumtext  not null
);

