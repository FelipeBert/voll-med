CREATE TABLE consultas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    marcada TINYINT NOT NULL,
    medico_id BIGINT,
    paciente_id BIGINT,

    PRIMARY KEY (id),
    FOREIGN KEY (medico_id) REFERENCES medicos(id),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
);
