DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS country;

CREATE TABLE IF NOT EXISTS country
(
    ID   SERIAL PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS city
(
    ID         SERIAL PRIMARY KEY,
    NAME       VARCHAR(250) NOT NULL,
    COUNTRY_ID INTEGER      NOT NULL
);

ALTER TABLE city
    ADD CONSTRAINT FK_COUNTRY FOREIGN KEY (COUNTRY_ID) REFERENCES country (ID);



-- 2. Populate all 195 country
INSERT INTO country (id, name) VALUES
(1, 'Afghanistan'),
(2, 'Albania'),
(3, 'Algeria'),
(4, 'Andorra'),
(5, 'Angola'),
(6, 'Antigua and Barbuda'),
(7, 'Argentina'),
(8, 'Armenia'),
(9, 'Australia'),
(10, 'Austria'),
(11, 'Azerbaijan'),
(12, 'Bahamas'),
(13, 'Bahrain'),
(14, 'Bangladesh'),
(15, 'Barbados'),
(16, 'Belarus'),
(17, 'Belgium'),
(18, 'Belize'),
(19, 'Benin'),
(20, 'Bhutan'),
(21, 'Bolivia'),
(22, 'Bosnia and Herzegovina'),
(23, 'Botswana'),
(24, 'Brazil'),
(25, 'Brunei'),
(26, 'Bulgaria'),
(27, 'Burkina Faso'),
(28, 'Burundi'),
(29, 'Cabo Verde'),
(30, 'Cambodia'),
(31, 'Cameroon'),
(32, 'Canada'),
(33, 'Central African Republic'),
(34, 'Chad'),
(35, 'Chile'),
(36, 'China'),
(37, 'Colombia'),
(38, 'Comoros'),
(39, 'Congo (Congo-Brazzaville)'),
(40, 'Costa Rica'),
(41, 'Croatia'),
(42, 'Cuba'),
(43, 'Cyprus'),
(44, 'Czechia (Czech Republic)'),
(45, 'Democratic Republic of the Congo'),
(46, 'Denmark'),
(47, 'Djibouti'),
(48, 'Dominica'),
(49, 'Dominican Republic'),
(50, 'Ecuador'),
(51, 'Egypt'),
(52, 'El Salvador'),
(53, 'Equatorial Guinea'),
(54, 'Eritrea'),
(55, 'Estonia'),
(56, 'Eswatini'),
(57, 'Ethiopia'),
(58, 'Fiji'),
(59, 'Finland'),
(60, 'France'),
(61, 'Gabon'),
(62, 'Gambia'),
(63, 'Georgia'),
(64, 'Germany'),
(65, 'Ghana'),
(66, 'Greece'),
(67, 'Grenada'),
(68, 'Guatemala'),
(69, 'Guinea'),
(70, 'Guinea-Bissau'),
(71, 'Guyana'),
(72, 'Haiti'),
(73, 'Honduras'),
(74, 'Hungary'),
(75, 'Iceland'),
(76, 'India'),
(77, 'Indonesia'),
(78, 'Iran'),
(79, 'Iraq'),
(80, 'Ireland'),
(81, 'Israel'),
(82, 'Italy'),
(83, 'Jamaica'),
(84, 'Japan'),
(85, 'Jordan'),
(86, 'Kazakhstan'),
(87, 'Kenya'),
(88, 'Kiribati'),
(89, 'Kuwait'),
(90, 'Kyrgyzstan'),
(91, 'Laos'),
(92, 'Latvia'),
(93, 'Lebanon'),
(94, 'Lesotho'),
(95, 'Liberia'),
(96, 'Libya'),
(97, 'Liechtenstein'),
(98, 'Lithuania'),
(99, 'Luxembourg'),
(100, 'Madagascar'),
(101, 'Malawi'),
(102, 'Malaysia'),
(103, 'Maldives'),
(104, 'Mali'),
(105, 'Malta'),
(106, 'Marshall Islands'),
(107, 'Mauritania'),
(108, 'Mauritius'),
(109, 'Mexico'),
(110, 'Micronesia'),
(111, 'Moldova'),
(112, 'Monaco'),
(113, 'Mongolia'),
(114, 'Montenegro'),
(115, 'Morocco'),
(116, 'Mozambique'),
(117, 'Myanmar (Burma)'),
(118, 'Namibia'),
(119, 'Nauru'),
(120, 'Nepal'),
(121, 'Netherlands'),
(122, 'New Zealand'),
(123, 'Nicaragua'),
(124, 'Niger'),
(125, 'Nigeria'),
(126, 'North Korea'),
(127, 'North Macedonia'),
(128, 'Norway'),
(129, 'Oman'),
(130, 'Pakistan'),
(131, 'Palau'),
(132, 'Palestine State'),
(133, 'Panama'),
(134, 'Papua New Guinea'),
(135, 'Paraguay'),
(136, 'Peru'),
(137, 'Philippines'),
(138, 'Poland'),
(139, 'Portugal'),
(140, 'Qatar'),
(141, 'Romania'),
(142, 'Russia'),
(143, 'Rwanda'),
(144, 'Saint Kitts and Nevis'),
(145, 'Saint Lucia'),
(146, 'Saint Vincent and the Grenadines'),
(147, 'Samoa'),
(148, 'San Marino'),
(149, 'Sao Tome and Principe'),
(150, 'Saudi Arabia'),
(151, 'Senegal'),
(152, 'Serbia'),
(153, 'Seychelles'),
(154, 'Sierra Leone'),
(155, 'Singapore'),
(156, 'Slovakia'),
(157, 'Slovenia'),
(158, 'Solomon Islands'),
(159, 'Somalia'),
(160, 'South Africa'),
(161, 'South Korea'),
(162, 'South Sudan'),
(163, 'Spain'),
(164, 'Sri Lanka'),
(165, 'Sudan'),
(166, 'Suriname'),
(167, 'Sweden'),
(168, 'Switzerland'),
(169, 'Syria'),
(170, 'Tajikistan'),
(171, 'Tanzania'),
(172, 'Thailand'),
(173, 'Timor-Leste'),
(174, 'Togo'),
(175, 'Tonga'),
(176, 'Trinidad and Tobago'),
(177, 'Tunisia'),
(178, 'Turkey'),
(179, 'Turkmenistan'),
(180, 'Tuvalu'),
(181, 'Uganda'),
(182, 'Ukraine'),
(183, 'United Arab Emirates'),
(184, 'United Kingdom'),
(185, 'United States'),
(186, 'Uruguay'),
(187, 'Uzbekistan'),
(188, 'Vanuatu'),
(189, 'Venezuela'),
(190, 'Vietnam'),
(191, 'Yemen'),
(192, 'Zambia'),
(193, 'Zimbabwe'),
(194, 'Holy See'),
(195, 'Monaco');


INSERT INTO city (name, country_id) VALUES
-- Argentina (country_id: 7)
('Buenos Aires', 7),
('Córdoba', 7),
('Rosario', 7),

-- Australia (country_id: 9)
('Sydney', 9),
('Melbourne', 9),
('Brisbane', 9),
('Perth', 9),

-- Austria (country_id: 10)
('Vienna', 10),
('Salzburg', 10),

-- Bangladesh (country_id: 14)
('Dhaka', 14),
('Chittagong', 14),

-- Belgium (country_id: 17)
('Brussels', 17),
('Antwerp', 17),

-- Brazil (country_id: 24)
('São Paulo', 24),
('Rio de Janeiro', 24),
('Brasília', 24),

-- Canada (country_id: 32)
('Toronto', 32),
('Vancouver', 32),
('Montreal', 32),
('Ottawa', 32),

-- Chile (country_id: 35)
('Santiago', 35),

-- China (country_id: 36)
('Beijing', 36),
('Shanghai', 36),
('Guangzhou', 36),
('Shenzhen', 36),

-- Colombia (country_id: 37)
('Bogotá', 37),
('Medellín', 37),

-- Denmark (country_id: 46)
('Copenhagen', 46),

-- Egypt (country_id: 51)
('Cairo', 51),
('Alexandria', 51),

-- Finland (country_id: 59)
('Helsinki', 59),

-- France (country_id: 60)
('Paris', 60),
('Marseille', 60),
('Lyon', 60),

-- Germany (country_id: 64)
('Berlin', 64),
('Munich', 64),
('Frankfurt', 64),
('Hamburg', 64),

-- Greece (country_id: 66)
('Athens', 66),

-- India (country_id: 76)
('New Delhi', 76),
('Mumbai', 76),
('Bangalore', 76),
('Hyderabad', 76),

-- Indonesia (country_id: 77)
('Jakarta', 77),

-- Ireland (country_id: 80)
('Dublin', 80),

-- Italy (country_id: 82)
('Rome', 82),
('Milan', 82),
('Florence', 82),

-- Japan (country_id: 84)
('Tokyo', 84),
('Osaka', 84),
('Kyoto', 84),

-- Malaysia (country_id: 102)
('Kuala Lumpur', 102),

-- Mexico (country_id: 109)
('Mexico city', 109),
('Guadalajara', 109),

-- Netherlands (country_id: 121)
('Amsterdam', 121),
('Rotterdam', 121),

-- New Zealand (country_id: 122)
('Auckland', 122),
('Wellington', 122),

-- Nigeria (country_id: 125)
('Lagos', 125),
('Abuja', 125),

-- Pakistan (country_id: 130)
('Karachi', 130),
('Lahore', 130),

-- Philippines (country_id: 137)
('Manila', 137),

-- Poland (country_id: 138)
('Warsaw', 138),

-- Portugal (country_id: 139)
('Lisbon', 139),

-- Qatar (country_id: 140)
('Doha', 140),

-- Russia (country_id: 142)
('Moscow', 142),
('Saint Petersburg', 142),

-- Saudi Arabia (country_id: 150)
('Riyadh', 150),
('Jeddah', 150),

-- Singapore (country_id: 155)
('Singapore', 155),

-- South Africa (country_id: 160)
('Johannesburg', 160),
('Cape Town', 160),

-- South Korea (country_id: 161)
('Seoul', 161),
('Busan', 161),

-- Spain (country_id: 163)
('Madrid', 163),
('Barcelona', 163),

-- Sweden (country_id: 167)
('Stockholm', 167),

-- Switzerland (country_id: 168)
('Zurich', 168),
('Geneva', 168),

-- Thailand (country_id: 172)
('Bangkok', 172),

-- Turkey (country_id: 178)
('Istanbul', 178),
('Ankara', 178),

-- Ukraine (country_id: 182)
('Kyiv', 182),

-- United Arab Emirates (country_id: 183)
('Dubai', 183),
('Abu Dhabi', 183),

-- United Kingdom (country_id: 184)
('London', 184),
('Manchester', 184),
('Edinburgh', 184),

-- United States (country_id: 185)
('New York', 185),
('Los Angeles', 185),
('Chicago', 185),
('Houston', 185),

-- Vietnam (country_id: 190)
('Ho Chi Minh city', 190),
('Hanoi', 190);
INSERT INTO city (name, country_id) VALUES
-- Algeria (country_id: 3)
('Algiers', 3),
('Oran', 3),

-- Argentina (country_id: 7)
('Mendoza', 7),
('La Plata', 7),

-- Australia (country_id: 9)
('Adelaide', 9),
('Canberra', 9),
('Hobart', 9),
('Darwin', 9),

-- Austria (country_id: 10)
('Graz', 10),
('Innsbruck', 10),

-- Bangladesh (country_id: 14)
('Sylhet', 14),
('Khulna', 14),

-- Belgium (country_id: 17)
('Ghent', 17),
('Liège', 17),
('Bruges', 17),

-- Brazil (country_id: 24)
('Salvador', 24),
('Fortaleza', 24),
('Belo Horizonte', 24),
('Curitiba', 24),
('Manaus', 24),

-- Canada (country_id: 32)
('Calgary', 32),
('Edmonton', 32),
('Quebec city', 32),
('Winnipeg', 32),
('Halifax', 32),

-- Chile (country_id: 35)
('Valparaíso', 35),
('Concepción', 35),

-- China (country_id: 36)
('Chengdu', 36),
('Wuhan', 36),
('Chongqing', 36),
('Xi''an', 36),
('Hangzhou', 36),
('Nanjing', 36),

-- Colombia (country_id: 37)
('Cali', 37),
('Barranquilla', 37),
('Cartagena', 37),

-- Denmark (country_id: 46)
('Aarhus', 46),
('Odense', 46),

-- Egypt (country_id: 51)
('Giza', 51),
('Port Said', 51),
('Luxor', 51),

-- Finland (country_id: 59)
('Tampere', 59),
('Turku', 59),

-- France (country_id: 60)
('Toulouse', 60),
('Nice', 60),
('Nantes', 60),
('Strasbourg', 60),
('Bordeaux', 60),

-- Germany (country_id: 64)
('Cologne', 64),
('Stuttgart', 64),
('Düsseldorf', 64),
('Leipzig', 64),
('Dortmund', 64),
('Essen', 64),

-- Greece (country_id: 66)
('Thessaloniki', 66),
('Patras', 66),

-- India (country_id: 76)
('Chennai', 76),
('Kolkata', 76),
('Ahmedabad', 76),
('Pune', 76),
('Surat', 76),
('Jaipur', 76),
('Lucknow', 76),

-- Indonesia (country_id: 77)
('Surabaya', 77),
('Bandung', 77),
('Medan', 77),
('Semarang', 77),

-- Ireland (country_id: 80)
('Cork', 80),
('Galway', 80),
('Limerick', 80),

-- Italy (country_id: 82)
('Naples', 82),
('Turin', 82),
('Palermo', 82),
('Genoa', 82),
('Bologna', 82),

-- Japan (country_id: 84)
('Yokohama', 84),
('Nagoya', 84),
('Sapporo', 84),
('Fukuoka', 84),
('Hiroshima', 84),
('Sendai', 84),

-- Malaysia (country_id: 102)
('Penang', 102),
('Johor Bahru', 102),
('Ipoh', 102),

-- Mexico (country_id: 109)
('Monterrey', 109),
('Puebla', 109),
('Tijuana', 109),
('León', 109),
('Cancún', 109),

-- Netherlands (country_id: 121)
('The Hague', 121),
('Utrecht', 121),
('Eindhoven', 121),

-- New Zealand (country_id: 122)
('Christchurch', 122),
('Hamilton', 122),

-- Nigeria (country_id: 125)
('Ibadan', 125),
('Kano', 125),
('Port Harcourt', 125),

-- Pakistan (country_id: 130)
('Faisalabad', 130),
('Rawalpindi', 130),
('Multan', 130),
('Peshawar', 130),

-- Philippines (country_id: 137)
('Quezon city', 137),
('Davao city', 137),
('Cebu city', 137),

-- Poland (country_id: 138)
('Kraków', 138),
('Łódź', 138),
('Wrocław', 138),

-- Portugal (country_id: 139)
('Porto', 139),
('Braga', 139),

-- Russia (country_id: 142)
('Novosibirsk', 142),
('Yekaterinburg', 142),
('Nizhny Novgorod', 142),
('Kazan', 142),

-- Saudi Arabia (country_id: 150)
('Mecca', 150),
('Medina', 150),
('Dammam', 150),

-- South Africa (country_id: 160)
('Durban', 160),
('Pretoria', 160),
('Port Elizabeth', 160),

-- South Korea (country_id: 161)
('Incheon', 161),
('Daegu', 161),
('Daejeon', 161),
('Gwangju', 161),

-- Spain (country_id: 163)
('Valencia', 163),
('Seville', 163),
('Zaragoza', 163),
('Málaga', 163),

-- Sweden (country_id: 167)
('Gothenburg', 167),
('Malmö', 167),

-- Switzerland (country_id: 168)
('Basel', 168),
('Lausanne', 168),

-- Thailand (country_id: 172)
('Chiang Mai', 172),
('Phuket', 172),
('Pattaya', 172),

-- Turkey (country_id: 178)
('Izmir', 178),
('Bursa', 178),
('Adana', 178),
('Gaziantep', 178),

-- Ukraine (country_id: 182)
('Kharkiv', 182),
('Odesa', 182),
('Dnipro', 182),

-- United Arab Emirates (country_id: 183)
('Sharjah', 183),
('Al Ain', 183),

-- United Kingdom (country_id: 184)
('Birmingham', 184),
('Glasgow', 184),
('Liverpool', 184),
('Leeds', 184),
('Belfast', 184),

-- United States (country_id: 185)
('Phoenix', 185),
('Philadelphia', 185),
('San Antonio', 185),
('San Diego', 185),
('Dallas', 185),
('San Jose', 185),
('Austin', 185),

-- Vietnam (country_id: 190)
('Da Nang', 190),
('Hai Phong', 190);


INSERT INTO city (name, country_id) VALUES
-- Afghanistan (country_id: 1)
('Kandahar', 1),

-- Albania (country_id: 2)
('Durrës', 2),
('Vlorë', 2),

-- Algeria (country_id: 3)
('Constantine', 3),

-- Andorra (country_id: 4)
('Escaldes-Engordany', 4),
('Encamp', 4),

-- Angola (country_id: 5)
('Benguela', 5),
('Huambo', 5),

-- Antigua and Barbuda (country_id: 6)
('All Saints', 6),
('Liberta', 6),

-- Armenia (country_id: 8)
('Vanadzor', 8),

-- Austria (country_id: 10)
('Linz', 10),

-- Azerbaijan (country_id: 11)
('Ganja', 11),
('Sumqayit', 11),

-- Bahamas (country_id: 12)
('Freeport', 12),
('West End', 12),

-- Bahrain (country_id: 13)
('Riffa', 13),
('Muharraq', 13),

-- Bangladesh (country_id: 14)
('Rajshahi', 14),

-- Barbados (country_id: 15)
('Speightstown', 15),
('Oistins', 15),

-- Belarus (country_id: 16)
('Gomel', 16),
('Mogilev', 16),

-- Belize (country_id: 18)
('Belize city', 18),
('San Ignacio', 18),

-- Benin (country_id: 19)
('Cotonou', 19),
('Parakou', 19),

-- Bhutan (country_id: 20)
('Phuntsholing', 20),
('Punakha', 20),

-- Bolivia (country_id: 21)
('Santa Cruz de la Sierra', 21),

-- Bosnia and Herzegovina (country_id: 22)
('Banja Luka', 22),
('Mostar', 22),

-- Botswana (country_id: 23)
('Francistown', 23),
('Molepolole', 23),

-- Brunei (country_id: 25)
('Kuala Belait', 25),
('Seria', 25),

-- Bulgaria (country_id: 26)
('Plovdiv', 26),
('Varna', 26),

-- Burkina Faso (country_id: 27)
('Bobo-Dioulasso', 27),
('Koudougou', 27),

-- Burundi (country_id: 28)
('Bujumbura', 28),
('Muyinga', 28),

-- Cabo Verde (country_id: 29)
('Mindelo', 29),
('Santa Maria', 29),

-- Cambodia (country_id: 30)
('Siem Reap', 30),
('Battambang', 30),

-- Cameroon (country_id: 31)
('Garoua', 31),

-- Central African Republic (country_id: 33)
('Bimbo', 33),
('Berbérati', 33),

-- Chad (country_id: 34)
('Moundou', 34),
('Sarh', 34),

-- Chile (country_id: 35)
('Viña del Mar', 35),
('Antofagasta', 35),

-- Comoros (country_id: 38)
('Mutsamudu', 38),
('Fomboni', 38),

-- Congo (Congo-Brazzaville) (country_id: 39)
('Pointe-Noire', 39),
('Dolisie', 39),

-- Costa Rica (country_id: 40)
('Alajuela', 40),
('Cartago', 40),

-- Croatia (country_id: 41)
('Split', 41),
('Rijeka', 41),

-- Cuba (country_id: 42)
('Santiago de Cuba', 42),
('Camagüey', 42),

-- Cyprus (country_id: 43)
('Limassol', 43),
('Larnaca', 43),

-- Czechia (Czech Republic) (country_id: 44)
('Brno', 44),
('Ostrava', 44),

-- Democratic Republic of the Congo (country_id: 45)
('Lubumbashi', 45),
('Mbuji-Mayi', 45),

-- Denmark (country_id: 46)
('Aalborg', 46),

-- Djibouti (country_id: 47)
('Ali Sabieh', 47),
('Tadjoura', 47),

-- Dominica (country_id: 48)
('Portsmouth', 48),
('Marigot', 48),

-- Dominican Republic (country_id: 49)
('Santiago de los Caballeros', 49),
('San Pedro de Macorís', 49),

-- Ecuador (country_id: 50)
('Cuenca', 50),

-- El Salvador (country_id: 52)
('Santa Ana', 52),
('San Miguel', 52),

-- Equatorial Guinea (country_id: 53)
('Bata', 53),
('Oyala', 53),

-- Eritrea (country_id: 54)
('Keren', 54),
('Massawa', 54),

-- Estonia (country_id: 55)
('Tartu', 55),
('Narva', 55),

-- Eswatini (country_id: 56)
('Manzini', 56),
('Big Bend', 56),

-- Ethiopia (country_id: 57)
('Dire Dawa', 57),
('Mekelle', 57),

-- Fiji (country_id: 58)
('Lautoka', 58),
('Nadi', 58),

-- Finland (country_id: 59)
('Oulu', 59),

-- Gabon (country_id: 61)
('Port-Gentil', 61),
('Franceville', 61),

-- Gambia (country_id: 62)
('Serekunda', 62),
('Brikama', 62),

-- Georgia (country_id: 63)
('Kutaisi', 63),
('Batumi', 63),

-- Ghana (country_id: 65)
('Kumasi', 65),
('Tamale', 65),

-- Grenada (country_id: 67)
('Gouyave', 67),
('Grenville', 67),

-- Guatemala (country_id: 68)
('Quetzaltenango', 68),
('Escuintla', 68),

-- Guinea (country_id: 69)
('Nzérékoré', 69),
('Kankan', 69),

-- Guinea-Bissau (country_id: 70)
('Bafatá', 70),
('Gabú', 70),

-- Guyana (country_id: 71)
('Linden', 71),
('New Amsterdam', 71),

-- Haiti (country_id: 72)
('Cap-Haïtien', 72),
('Les Cayes', 72),

-- Honduras (country_id: 73)
('San Pedro Sula', 73),
('La Ceiba', 73),

-- Hungary (country_id: 74)
('Debrecen', 74),
('Szeged', 74),

-- Iceland (country_id: 75)
('Akureyri', 75),
('Hafnarfjörður', 75),

-- Iran (country_id: 78)
('Mashhad', 78),

-- Iraq (country_id: 79)
('Basra', 79),
('Erbil', 79),

-- Israel (country_id: 81)
('Haifa', 81),

-- Jamaica (country_id: 83)
('Montego Bay', 83),
('Spanish Town', 83),

-- Jordan (country_id: 85)
('Zarqa', 85),
('Irbid', 85),

-- Kazakhstan (country_id: 86)
('Shymkent', 86),

-- Kenya (country_id: 87)
('Mombasa', 87),
('Kisumu', 87),

-- Kiribati (country_id: 88)
('Betio', 88),
('Bikenibeu', 88),

-- Kuwait (country_id: 89)
('Jahra', 89),
('Hawalli', 89),

-- Kyrgyzstan (country_id: 90)
('Osh', 90),
('Jalal-Abad', 90),

-- Laos (country_id: 91)
('Pakse', 91),
('Luang Prabang', 91),

-- Latvia (country_id: 92)
('Daugavpils', 92),
('Liepāja', 92),

-- Lebanon (country_id: 93)
('Tripoli', 93),
('Sidon', 93),

-- Lesotho (country_id: 94)
('Teyateyaneng', 94),
('Mafeteng', 94),

-- Liberia (country_id: 95)
('Gbarnga', 95),
('Buchanan', 95),

-- Libya (country_id: 96)
('Benghazi', 96),
('Misrata', 96),

-- Liechtenstein (country_id: 97)
('Schaan', 97),
('Triesen', 97),

-- Lithuania (country_id: 98)
('Kaunas', 98),
('Klaipėda', 98),

-- Luxembourg (country_id: 99)
('Esch-sur-Alzette', 99),
('Differdange', 99),

-- Madagascar (country_id: 100)
('Toamasina', 100),
('Antsirabe', 100),

-- Malawi (country_id: 101)
('Blantyre', 101),
('Mzuzu', 101),

-- Maldives (country_id: 103)
('Hithadhoo', 103),
('Kulhudhuffushi', 103),

-- Mali (country_id: 104)
('Sikasso', 104),
('Mopti', 104),

-- Malta (country_id: 105)
('Mdina', 105),
('Sliema', 105),

-- Marshall Islands (country_id: 106)
('Ebeye', 106),
('Jaluit', 106),

-- Mauritania (country_id: 107)
('Nouadhibou', 107),
('Kédi', 107),

-- Mauritius (country_id: 108)
('Beau Bassin-Rose Hill', 108),
('Vacoas-Phoenix', 108),

-- Micronesia (country_id: 110)
('Weno', 110),
('Kolonia', 110),

-- Moldova (country_id: 111)
('Bălți', 111),
('Tiraspol', 111),

-- Monaco (country_id: 112)
('La Condamine', 112),
('Fontvieille', 112),

-- Mongolia (country_id: 113)
('Erdenet', 113),
('Darkhan', 113),

-- Montenegro (country_id: 114)
('Nikšić', 114),
('Pljevlja', 114),

-- Morocco (country_id: 115)
('Fes', 115),

-- Mozambique (country_id: 116)
('Beira', 116),
('Nampula', 116),

-- Myanmar (Burma) (country_id: 117)
('Mandalay', 117),

-- Namibia (country_id: 118)
('Walvis Bay', 118),
('Swakopmund', 118),

-- Nauru (country_id: 119)
('Aiwo', 119),
('Boe', 119),

-- Nepal (country_id: 120)
('Pokhara', 120),
('Lalitpur', 120),

-- Nicaragua (country_id: 123)
('León', 123),
('Chinandega', 123),

-- Niger (country_id: 124)
('Zinder', 124),
('Maradi', 124),

-- North Korea (country_id: 126)
('Hamhung', 126),
('Chongjin', 126),

-- North Macedonia (country_id: 127)
('Bitola', 127),
('Kumanovo', 127),

-- Norway (country_id: 128)
('Bergen', 128),
('Trondheim', 128),

-- Oman (country_id: 129)
('Salalah', 129),
('Sohar', 129),

-- Palau (country_id: 131)
('Koror', 131),
('Meyuns', 131),

-- Palestine State (country_id: 132)
('Gaza city', 132),
('Hebron', 132),

-- Panama (country_id: 133)
('Colón', 133),
('David', 133),

-- Papua New Guinea (country_id: 134)
('Lae', 134),
('Mount Hagen', 134),

-- Paraguay (country_id: 135)
('Ciudad del Este', 135),
('San Lorenzo', 135),

-- Peru (country_id: 136)
('Arequipa', 136),
('Trujillo', 136),

-- Qatar (country_id: 140)
('Al Wakrah', 140),
('Al Khor', 140),

-- Romania (country_id: 141)
('Cluj-Napoca', 141),
('Timișoara', 141),

-- Rwanda (country_id: 143)
('Gisenyi', 143),
('Butare', 143),

-- Saint Kitts and Nevis (country_id: 144)
('Sandy Point Town', 144),
('Charlestown', 144),

-- Saint Lucia (country_id: 145)
('Gros Islet', 145),
('Vieux Fort', 145),

-- Saint Vincent and the Grenadines (country_id: 146)
('Georgetown', 146),
('Barrouallie', 146),

-- Samoa (country_id: 147)
('Asau', 147),
('Afega', 147),

-- San Marino (country_id: 148)
('Serravalle', 148),
('Borgo Maggiore', 148),

-- Sao Tome and Principe (country_id: 149)
('Trindade', 149),
('Santana', 149),

-- Senegal (country_id: 151)
('Touba', 151),
('Thiès', 151),

-- Serbia (country_id: 152)
('Novi Sad', 152),
('Niš', 152),

-- Seychelles (country_id: 153)
('Anse Royale', 153),
('Bel Ombre', 153),

-- Sierra Leone (country_id: 154)
('Bo', 154),
('Kenema', 154),

-- Slovakia (country_id: 156)
('Košice', 156),
('Prešov', 156),

-- Slovenia (country_id: 157)
('Maribor', 157),
('Celje', 157),

-- Solomon Islands (country_id: 158)
('Gizo', 158),
('Auki', 158),

-- Somalia (country_id: 159)
('Hargeisa', 159),
('Bosaso', 159),

-- South Sudan (country_id: 162)
('Malakal', 162),
('Wau', 162);

-- 1. Remove previous sparse testing placeholders for Armenia to prevent Primary Key errors
DELETE FROM city WHERE country_id = 8;

-- 2. Populate all remaining officially designated city and towns of Armenia (country_id: 8)
INSERT INTO city (name, country_id) VALUES
-- Major Urban Centers & Republic Subordination
('Yerevan', 8),
('Gyumri', 8),
('Vanadzor', 8),

-- Aragatsotn Province
('Ashtarak', 8),
('Aparan', 8),
('Talin', 8),

-- Ararat Province
('Artashat', 8),
('Ararat', 8),
('Masis', 8),
('Vedi', 8),

-- Armavir Province
('Vagharshapat', 8), -- (Etchmiadzin)
('Armavir', 8),
('Metsamor', 8),

-- Gegharkunik Province
('Gavar', 8),
('Sevan', 8),
('Martuni', 8),
('Vardenis', 8),
('Chambarak', 8),

-- Kotayk Province
('Hrazdan', 8),
('Abovyan', 8),
('Charentsavan', 8),
('Yeghvard', 8),
('Nor Hachn', 8),
('Byureghavan', 8),
('Tsaghkadzor', 8),

-- Lori Province
('Alaverdi', 8),
('Stepanavan', 8),
('Spitak', 8),
('Tashir', 8),
('Akhtala', 8),
('Tumanyan', 8),
('Shamlukh', 8),

-- Shirak Province
('Artik', 8),
('Maralik', 8),

-- Syunik Province
('Kapan', 8),
('Goris', 8),
('Sisian', 8),
('Kajaran', 8),
('Meghri', 8),
('Agarak', 8),
('Dastakert', 8),

-- Tavush Province
('Ijevan', 8),
('Dilijan', 8),
('Berd', 8),
('Noyemberyan', 8),
('Ayrum', 8),

-- Vayots Dzor Province
('Yeghegnadzor', 8),
('Vayk', 8),
('Jermuk', 8);
