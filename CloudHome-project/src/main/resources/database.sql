create table if not exists `WiseGuardCloudHome-Project`.CHome_Alarm_Message
(
    alarm_id        varchar(18)   not null comment '警报id'
        primary key,
    alarm_type      varchar(50)   null comment '警报类型',
    alarm_text      varchar(255)  null comment '警报内容',
    alarm_level     tinyint       null comment '警报等级',
    alarm_status    int default 0 null comment '警报状态（0未确认 1已确认）',
    alarm_source_id varchar(20)   null comment '关联设备id',
    confirm_user    int           null comment '确认人',
    confirm_time    datetime      null comment '确认时间',
    alarm_time      datetime      null comment '警报时间'
)
    comment '异常报警数据记录表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_Furniture
(
    furniture_id     int auto_increment comment '家具 ID'
        primary key,
    device_id        varchar(100) not null comment '设备id',
    furniture_name   varchar(100) not null comment '家具名',
    furniture_type   int          not null comment '设备类型',
    furniture_status tinyint      null comment '设备状态(0：关闭，1：开启)',
    location_id      int          null comment '所属房间id',
    remark           varchar(255) null comment '备注',
    created_user     int          null comment '创建者',
    created_time     datetime     not null comment '创建时间',
    update_user      int          null comment '更新者',
    update_time      datetime     null comment '更新时间',
    delete_user      int          null comment '删除者',
    delete_time      datetime     null comment '删除时间',
    is_delete        varchar(1)   null comment '是否删除',
    constraint device_id
        unique (device_id)
)
    comment '设备信息表';

create index CHome_Furniture_furniture_type_index
    on `WiseGuardCloudHome-Project`.CHome_Furniture (furniture_type);

create table if not exists `WiseGuardCloudHome-Project`.CHome_Furniture_Power
(
    power_id        int auto_increment comment '能力 id'
        primary key,
    device_power_id varchar(50) null comment '关联设备Id',
    power_name      varchar(50) null comment '能力名称',
    power_value     double      null comment '能力值',
    unit            varchar(20) null comment '单位',
    power_type      int         null comment '能力类型(1.控制类  2.环境监测类)',
    operation_code  varchar(12) null comment '操作码',
    update_time     datetime    null comment '更新时间',
    constraint CHome_Furniture_Power_CHome_Furniture_device_id_fk
        foreign key (device_power_id) references `WiseGuardCloudHome-Project`.CHome_Furniture (device_id)
)
    comment '设备能力表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_Furniture_Type
(
    type_id   int auto_increment comment 'id'
        primary key,
    type_name varchar(100) null comment '类型名称'
)
    comment '设备类型表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_Location
(
    id           int auto_increment comment 'ID'
        primary key,
    room_name    varchar(50) not null comment '房间名称',
    created_time datetime    null comment '创建时间'
)
    comment '家居房间位置表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_Menu
(
    id        int               not null comment '编号'
        primary key,
    pid       int               null comment '父级编号',
    name      varchar(255)      null comment '权限名称',
    code      varchar(255)      null comment '权限编码',
    type      tinyint           null comment '类型 0菜单 1权限 2url',
    is_delete tinyint default 0 not null comment '是否删除 0未删除 1已删除'
)
    comment '权限信息表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_Role
(
    id        int auto_increment comment '角色id'
        primary key,
    role_name varchar(255) null comment '角色名称',
    remark    varchar(255) null comment '备注'
)
    comment '角色信息表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_User
(
    user_id        int auto_increment comment '用户ID'
        primary key,
    user_name      varchar(50)            not null comment '用户名',
    user_password  varchar(255)           not null comment '用户密码',
    user_avatar    varchar(255)           null comment '用户头像',
    user_gender    tinyint(1) default 0   null comment '用户性别(0未知;1男;2女)',
    user_age       tinyint                null comment '用户年龄',
    user_region    varchar(50)            null comment '用户所属区域',
    user_ip_region varchar(50)            null comment '用户Ip属地',
    remark         varchar(255)           null comment '用户备注',
    user_phone     varchar(11)            null comment '手机号码',
    user_status    varchar(1) default '1' null comment '用户状态(0停用;1启用)',
    created_time   datetime               not null comment '创建时间',
    created_user   int                    null comment '创建者',
    update_time    datetime               null comment '更新时间',
    update_user    int                    null comment '更新者',
    delete_time    datetime               null comment '删除时间',
    delete_user    int                    null comment '删除者',
    is_Delete      tinyint(1) default 0   null comment '是否删除',
    constraint CHome_User_user_name_uindex
        unique (user_name),
    constraint CHome_User_user_phone_uindex
        unique (user_phone)
)
    comment '用户信息表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_role_menu
(
    rid int null comment '角色id',
    mid int null comment '权限id',
    constraint CHome_role_menu_CHome_menu_id_fk
        foreign key (mid) references `WiseGuardCloudHome-Project`.CHome_Menu (id),
    constraint CHome_role_menu_CHome_role_role_id_fk
        foreign key (rid) references `WiseGuardCloudHome-Project`.CHome_Role (id)
)
    comment '角色权限关联表';

create table if not exists `WiseGuardCloudHome-Project`.CHome_user_role
(
    uid int null comment '用户id',
    rid int null comment '角色id',
    constraint CHome_user_role_CHome_User_user_id_fk
        foreign key (uid) references `WiseGuardCloudHome-Project`.CHome_User (user_id),
    constraint CHome_user_role_CHome_role_role_id_fk
        foreign key (rid) references `WiseGuardCloudHome-Project`.CHome_Role (id)
)
    comment '用户角色关联表';


-- 创建城市编码表,需要在高德地图官网找的对应的文件导入
create table if not exists `WiseGuardCloudHome-Project`.City_Info
(
    id        int auto_increment comment '主键'
        primary key,
    city_name varchar(50) null comment '城市名',
    adcode    int         null comment '城市编码'
)
    comment '城市编码表';

