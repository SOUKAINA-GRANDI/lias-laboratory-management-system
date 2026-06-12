USE lias_db;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE chef_equipe_mandats;
TRUNCATE TABLE role_historique;
TRUNCATE TABLE affiliation_historique;
TRUNCATE TABLE mandats;
TRUNCATE TABLE utilisateurs;
TRUNCATE TABLE membres;
TRUNCATE TABLE equipes;

SET FOREIGN_KEY_CHECKS = 1;

-- ======================
-- EQUIPES
-- ======================

INSERT INTO equipes (nom, acronyme, description, active, dateCreation)
VALUES
('Intelligence Artificielle','IA','Recherche IA',1,'2015-01-01'),
('Big Data & Cloud','BDCC','Recherche Big Data',1,'2016-03-15'),
('Cybersécurité','CYBER','Sécurité informatique',1,'2017-09-01'),
('IoT & Embarqué','IOT','Recherche IoT',1,'2018-02-20');

-- ======================
-- MEMBRES
-- ======================

INSERT INTO membres
(nom, prenom, email, statut, dateEmbauche, dateAffiliation,
grade, specialite, actif, dateCreation, dateModification, equipe_actuelle_id)
VALUES
('EL HABTI','Hassan','h.elhabti@lias.ma','PERMANENT','2000-09-01','2015-01-01',
'Professeur','IA',1,NOW(),NOW(),1),

('ALAMI','Mohammed','m.alami@lias.ma','PERMANENT','2010-09-01','2015-01-01',
'Professeur','Machine Learning',1,NOW(),NOW(),1),

('BENNANI','Fatima','f.bennani@lias.ma','PERMANENT','2008-09-01','2016-03-15',
'Professeur','Big Data',1,NOW(),NOW(),2),

('TAZI','Ahmed','a.tazi@lias.ma','PERMANENT','2009-09-01','2017-09-01',
'Professeur','Cybersécurité',1,NOW(),NOW(),3),

('LAMRANI','Amina','a.lamrani@lias.ma','DOCTORANT','2022-09-01','2022-09-01',
'Doctorant','IoT',1,NOW(),NOW(),4);

-- ======================
-- AFFILIATION HISTORIQUE
-- ======================

INSERT INTO affiliation_historique
(membre_id, dateDebut, dateFin, statutPendantPeriode, periodeActive)
SELECT id, dateAffiliation, NULL, statut, 1 FROM membres;

-- ======================
-- MANDATS
-- ======================

INSERT INTO mandats
(dateDebut, dateFin, description, directeur_id, vice_directeur_id)
VALUES
('2020-01-01','2024-12-31','Mandat 2020-2024',1,2),
('2025-01-01',NULL,'Mandat 2025-2029',1,3);

-- ======================
-- CHEF EQUIPE MANDATS
-- ======================

INSERT INTO chef_equipe_mandats
(membre_id, equipe_id, mandat_id, dateDebut, dateFin)
VALUES
(1,1,1,'2020-01-01','2024-12-31'),
(2,2,1,'2020-01-01','2024-12-31'),
(3,3,1,'2020-01-01','2024-12-31'),
(4,4,1,'2020-01-01','2024-12-31'),

(1,1,2,'2025-01-01',NULL),
(2,2,2,'2025-01-01',NULL),
(3,3,2,'2025-01-01',NULL),
(4,4,2,'2025-01-01',NULL);

-- ======================
-- ROLE HISTORIQUE
-- ======================

INSERT INTO role_historique
(membre_id, role, dateDebut, dateFin)
VALUES
(1,'DIRECTEUR','2020-01-01',NULL),
(2,'CHEF_EQUIPE','2020-01-01',NULL),
(3,'CHEF_EQUIPE','2020-01-01',NULL),
(4,'CHEF_EQUIPE','2020-01-01',NULL);

-- ======================
-- UTILISATEURS
-- ======================

INSERT INTO utilisateurs
(membre_id, username, password, typeUtilisateur,
compteActif, premiereConnexion, dateCreation, dateModification)
VALUES
(1,'admin','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lY4i',
'ADMINISTRATEUR',1,0,NOW(),NOW()),

(2,'m.alami','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lY4i',
'DIRECTEUR',1,1,NOW(),NOW());