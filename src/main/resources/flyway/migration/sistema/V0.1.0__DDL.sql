CREATE SEQUENCE hibernate_sequence
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- revinfo definition

CREATE TABLE revinfo (
	id int4 NOT NULL,
	"timestamp" int8 NOT NULL,
	id_user uuid NULL,
	ip varchar(255) NULL,
	CONSTRAINT revinfo_pkey PRIMARY KEY (id)
);


-- t_parametro definition

CREATE TABLE t_parametro (
	id_parametro uuid NOT NULL,
	insert_date timestamp NULL,
	update_date timestamp NULL,
	"version" int8 NULL,
	descricao varchar(255) NULL,
	numero int4 NULL,
	tipo varchar(255) NULL,
	valor varchar(255) NULL,
	CONSTRAINT t_parametro_pkey PRIMARY KEY (id_parametro),
	CONSTRAINT uk_a7fbv4rd845tswcnnv51b0ry3 UNIQUE (numero)
);


-- t_processamento definition

CREATE TABLE t_processamento (
	id_processamento uuid NOT NULL,
	insert_date timestamp NULL,
	update_date timestamp NULL,
	"version" int8 NULL,
	linha int8 NOT NULL,
	payload varchar(255) NULL,
	response varchar(255) NULL,
	solicitacao_id uuid NULL,
	status varchar(255) NULL,
	CONSTRAINT t_processamento_pkey PRIMARY KEY (id_processamento)
);


-- t_auditoria_http definition

CREATE TABLE t_auditoria_http (
	id_auditoria_http uuid NOT NULL,
	insert_date timestamp NULL,
	update_date timestamp NULL,
	"version" int8 NULL,
	http_method varchar(255) NULL,
	http_status int4 NOT NULL,
	request_body text NULL,
	request_timestamp timestamp NULL,
	response_body text NULL,
	response_timestamp timestamp NULL,
	url varchar(255) NULL,
	id_processamento uuid NULL,
	CONSTRAINT t_auditoria_http_pkey PRIMARY KEY (id_auditoria_http),
	CONSTRAINT fklmqejfu16s9m15r1kry8rfkxt FOREIGN KEY (id_processamento) REFERENCES t_processamento(id_processamento)
);


-- t_parametro_aud definition

CREATE TABLE t_parametro_aud (
	id_parametro uuid NOT NULL,
	rev int4 NOT NULL,
	revtype int2 NULL,
	CONSTRAINT t_parametro_aud_pkey PRIMARY KEY (id_parametro, rev),
	CONSTRAINT fk2yq53rnx76uwku90fib80o5aw FOREIGN KEY (rev) REFERENCES revinfo(id)
);


-- t_transacao definition

CREATE TABLE t_transacao (
	id_transacao uuid NOT NULL,
	insert_date timestamp NULL,
	update_date timestamp NULL,
	"version" int8 NULL,
	"data" timestamp NULL,
	operacao varchar(255) NULL,
	id_processamento uuid NULL,
	CONSTRAINT t_transacao_pkey PRIMARY KEY (id_transacao),
	CONSTRAINT fk94edrtcmexwn76nn2j27ubb58 FOREIGN KEY (id_processamento) REFERENCES t_processamento(id_processamento)
);
