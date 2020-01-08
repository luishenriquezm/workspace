INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('andres','$2a$10$G5yTu4B.XClwnsI6LCj3UuQS1anfTyZHYt4XYzI64qMBb0Y6dx0Su', true, 'Andres', 'Guzman', 'andres@gmail.com')
INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('admin','$2a$10$PQk6C5xmY661nKC85kL9iumSuyAPg0cq5w17mpmdWAHRpQF.PgtNG', true, 'John', 'Doe', 'jhon.doegmail.com')

INSERT INTO roles (nombre) VALUES ('ROLE_USER') 
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN')


INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (1,1)
INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (2,2)
INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (2,1)

