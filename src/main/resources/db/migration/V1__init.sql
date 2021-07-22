create table bot_states
(
    id          int  not null primary key,
    description text not null
);
insert into bot_states (id, description) values (0,'READY');
insert into bot_states (id, description) values (1,'BUSY');
create table bot_users
(
    id         bigserial   primary key,
    user_name  text        not null,
    chat_id    bigserial   not null,
    bot_state  int         not null references bot_states (id)
);
create unique index users_user_name_uindex on bot_users (user_name);
create unique index users_chat_id_uindex on bot_users (chat_id);
create table media_type(
    id          int  not null primary key,
    type        text not null
);
insert into media_type (id, type) values (0,'mp3');
insert into media_type (id, type) values (1,'mp4');
create table uploaded_files(
    id                  bigserial  primary key,
    youtube_video_id    text       not null,
    telegram_file_id    text       not null,
    media_type          int        not null  references media_type (id)
);
create unique index video_id_telegram_id_type_uindex
    on uploaded_files(youtube_video_id, telegram_file_id, media_type);