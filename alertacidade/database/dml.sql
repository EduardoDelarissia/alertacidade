-- =====================================================================
-- AlertaCidade - Script DML (dados de exemplo)
-- ATENCAO: todos os registros abaixo sao FICTICIOS, criados apenas para
-- demonstrar o sistema. Nomes de pontos, coordenadas, leituras e alertas
-- nao representam medicoes reais. Substituir pelo bairro escolhido pela
-- equipe quando a pesquisa de campo estiver concluida.
-- Executar apos o ddl.sql. IDs sao gerados por IDENTITY; as FKs sao
-- resolvidas por subconsulta para o script nao depender de numeracao.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Pontos de monitoramento (6)
-- Regra de classificacao: valor >= cota critica -> CRITICO;
--                         valor >= cota atencao -> ATENCAO; senao NORMAL
-- ---------------------------------------------------------------------
INSERT INTO T_AC_PONTO_MONITORAMENTO (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES ('Canal Rua das Palmeiras', 'Jardim Pantanal', 'Sao Paulo', -23.497100, -46.437200, 80, 120, 'S');

INSERT INTO T_AC_PONTO_MONITORAMENTO (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES ('Corrego Avenida Central', 'Jardim Pantanal', 'Sao Paulo', -23.499800, -46.441500, 60, 100, 'S');

INSERT INTO T_AC_PONTO_MONITORAMENTO (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES ('Passagem Subterranea Norte', 'Vila Prudente', 'Sao Paulo', -23.583400, -46.580900, 30, 50, 'S');

INSERT INTO T_AC_PONTO_MONITORAMENTO (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES ('Ponte da Escola Municipal', 'Itaim Paulista', 'Sao Paulo', -23.501200, -46.398700, 70, 110, 'S');

INSERT INTO T_AC_PONTO_MONITORAMENTO (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES ('Bueiro Praca do Mercado', 'Vila Prudente', 'Sao Paulo', -23.586100, -46.577300, 40, 70, 'S');

-- Ponto desativado (sem sensores): usado para demonstrar o filtro WHERE da consulta 3 do dql.sql
INSERT INTO T_AC_PONTO_MONITORAMENTO (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude, nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
VALUES ('Galeria Rua do Comercio', 'Itaim Paulista', 'Sao Paulo', -23.498400, -46.401900, 50, 90, 'N');

-- ---------------------------------------------------------------------
-- Sensores (6)
-- ---------------------------------------------------------------------
INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Canal Rua das Palmeiras'),
        'S-PAL-NIV-01', 'NIVEL_AGUA', 'cm', 'ATIVO', DATE '2026-10-20');

INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Canal Rua das Palmeiras'),
        'S-PAL-PLU-01', 'PLUVIOMETRO', 'mm', 'ATIVO', DATE '2026-10-20');

INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Corrego Avenida Central'),
        'S-CEN-NIV-01', 'NIVEL_AGUA', 'cm', 'ATIVO', DATE '2026-10-21');

INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Passagem Subterranea Norte'),
        'S-PSN-NIV-01', 'NIVEL_AGUA', 'cm', 'ATIVO', DATE '2026-10-22');

INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Ponte da Escola Municipal'),
        'S-ESC-NIV-01', 'NIVEL_AGUA', 'cm', 'MANUTENCAO', DATE '2026-10-23');

INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Bueiro Praca do Mercado'),
        'S-MER-NIV-01', 'NIVEL_AGUA', 'cm', 'INATIVO', DATE '2026-10-23');

-- ---------------------------------------------------------------------
-- Medicoes (15) - cenario simulado de chuva forte em 03/11/2026
-- ---------------------------------------------------------------------
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-NIV-01'), 35.00, TIMESTAMP '2026-11-03 14:00:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-NIV-01'), 62.00, TIMESTAMP '2026-11-03 14:30:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-NIV-01'), 88.00, TIMESTAMP '2026-11-03 15:00:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-NIV-01'), 126.00, TIMESTAMP '2026-11-03 15:30:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-NIV-01'), 74.00, TIMESTAMP '2026-11-03 17:00:00', 'SIMULADOR');

INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-PLU-01'), 4.20, TIMESTAMP '2026-11-03 14:00:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-PLU-01'), 12.80, TIMESTAMP '2026-11-03 14:30:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PAL-PLU-01'), 31.50, TIMESTAMP '2026-11-03 15:00:00', 'SIMULADOR');

INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-CEN-NIV-01'), 40.00, TIMESTAMP '2026-11-03 14:00:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-CEN-NIV-01'), 55.00, TIMESTAMP '2026-11-03 14:30:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-CEN-NIV-01'), 72.00, TIMESTAMP '2026-11-03 15:00:00', 'SIMULADOR');

INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PSN-NIV-01'), 12.00, TIMESTAMP '2026-11-03 14:00:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PSN-NIV-01'), 28.00, TIMESTAMP '2026-11-03 14:30:00', 'SIMULADOR');
INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-PSN-NIV-01'), 51.00, TIMESTAMP '2026-11-03 15:00:00', 'SIMULADOR');

INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
VALUES ((SELECT id_sensor FROM T_AC_SENSOR WHERE cd_sensor = 'S-ESC-NIV-01'), 20.00, TIMESTAMP '2026-11-02 10:00:00', 'MANUAL');

-- ---------------------------------------------------------------------
-- Alertas (5) - coerentes com as medicoes e cotas acima
-- ---------------------------------------------------------------------
-- 88 cm >= 80 (atencao) no Canal Rua das Palmeiras; encerrado quando voltou a 74 cm
INSERT INTO T_AC_ALERTA (id_ponto, id_medicao, nv_alerta, ds_mensagem, dt_hora_alerta, st_alerta, dt_encerramento)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Canal Rua das Palmeiras'),
        (SELECT m.id_medicao FROM T_AC_MEDICAO m JOIN T_AC_SENSOR s ON s.id_sensor = m.id_sensor
          WHERE s.cd_sensor = 'S-PAL-NIV-01' AND m.dt_hora_medicao = TIMESTAMP '2026-11-03 15:00:00'),
        'ATENCAO', 'Nivel de agua em 88 cm (cota de atencao: 80 cm)',
        TIMESTAMP '2026-11-03 15:00:00', 'ENCERRADO', TIMESTAMP '2026-11-03 17:00:00');

-- 126 cm >= 120 (critica)
INSERT INTO T_AC_ALERTA (id_ponto, id_medicao, nv_alerta, ds_mensagem, dt_hora_alerta, st_alerta, dt_encerramento)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Canal Rua das Palmeiras'),
        (SELECT m.id_medicao FROM T_AC_MEDICAO m JOIN T_AC_SENSOR s ON s.id_sensor = m.id_sensor
          WHERE s.cd_sensor = 'S-PAL-NIV-01' AND m.dt_hora_medicao = TIMESTAMP '2026-11-03 15:30:00'),
        'CRITICO', 'Nivel de agua em 126 cm (cota critica: 120 cm)',
        TIMESTAMP '2026-11-03 15:30:00', 'ENCERRADO', TIMESTAMP '2026-11-03 17:00:00');

-- 72 cm >= 60 (atencao) no Corrego Avenida Central
INSERT INTO T_AC_ALERTA (id_ponto, id_medicao, nv_alerta, ds_mensagem, dt_hora_alerta, st_alerta, dt_encerramento)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Corrego Avenida Central'),
        (SELECT m.id_medicao FROM T_AC_MEDICAO m JOIN T_AC_SENSOR s ON s.id_sensor = m.id_sensor
          WHERE s.cd_sensor = 'S-CEN-NIV-01' AND m.dt_hora_medicao = TIMESTAMP '2026-11-03 15:00:00'),
        'ATENCAO', 'Nivel de agua em 72 cm (cota de atencao: 60 cm)',
        TIMESTAMP '2026-11-03 15:00:00', 'ABERTO', NULL);

-- 51 cm >= 50 (critica) na Passagem Subterranea Norte
INSERT INTO T_AC_ALERTA (id_ponto, id_medicao, nv_alerta, ds_mensagem, dt_hora_alerta, st_alerta, dt_encerramento)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Passagem Subterranea Norte'),
        (SELECT m.id_medicao FROM T_AC_MEDICAO m JOIN T_AC_SENSOR s ON s.id_sensor = m.id_sensor
          WHERE s.cd_sensor = 'S-PSN-NIV-01' AND m.dt_hora_medicao = TIMESTAMP '2026-11-03 15:00:00'),
        'CRITICO', 'Nivel de agua em 51 cm (cota critica: 50 cm)',
        TIMESTAMP '2026-11-03 15:00:00', 'ABERTO', NULL);

-- Alerta manual (sem medicao de origem): sensor da ponte em manutencao
INSERT INTO T_AC_ALERTA (id_ponto, id_medicao, nv_alerta, ds_mensagem, dt_hora_alerta, st_alerta, dt_encerramento)
VALUES ((SELECT id_ponto FROM T_AC_PONTO_MONITORAMENTO WHERE nm_ponto = 'Ponte da Escola Municipal'),
        NULL, 'ATENCAO', 'Alerta manual: sensor em manutencao, acompanhamento visual do ponto',
        TIMESTAMP '2026-11-03 15:10:00', 'ENCERRADO', TIMESTAMP '2026-11-03 18:00:00');

COMMIT;
