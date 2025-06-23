--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: Adventure; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "Adventure" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en-GB';


ALTER DATABASE "Adventure" OWNER TO postgres;

\connect "Adventure"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: adventurer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.adventurer (
    adventurer_id integer NOT NULL,
    adventurer_hp integer,
    adventurer_class character varying(50),
    adventurer_password bigint,
    max_health integer,
    adventurer_name character varying(50) NOT NULL,
    weapon_id integer DEFAULT 1 NOT NULL,
    basic_potions integer DEFAULT 10,
    max_potions integer DEFAULT 5,
    defeated_monsters integer DEFAULT 0,
    adventurer_level integer DEFAULT 1,
    adventurer_exp integer DEFAULT 0,
    crit_chance double precision DEFAULT 0.4
);


ALTER TABLE public.adventurer OWNER TO postgres;

--
-- Name: adventurer_adventurer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.adventurer_adventurer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.adventurer_adventurer_id_seq OWNER TO postgres;

--
-- Name: adventurer_adventurer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.adventurer_adventurer_id_seq OWNED BY public.adventurer.adventurer_id;


--
-- Name: monsters; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.monsters (
    monster_id integer NOT NULL,
    monster_name character varying(50) NOT NULL,
    monster_damage integer,
    monster_hp integer,
    monster_drop_basic integer,
    monster_drop_max integer,
    monster_drop_exp integer
);


ALTER TABLE public.monsters OWNER TO postgres;

--
-- Name: monsters_monster_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.monsters_monster_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.monsters_monster_id_seq OWNER TO postgres;

--
-- Name: monsters_monster_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.monsters_monster_id_seq OWNED BY public.monsters.monster_id;


--
-- Name: weapons; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.weapons (
    weapon_id integer NOT NULL,
    weapon_name character varying(50),
    weapon_damage integer,
    weapon_level_requirement integer,
    weapon_class character varying(50) DEFAULT 'Warrior'::character varying NOT NULL
);


ALTER TABLE public.weapons OWNER TO postgres;

--
-- Name: weapons_weapon_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.weapons_weapon_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.weapons_weapon_id_seq OWNER TO postgres;

--
-- Name: weapons_weapon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.weapons_weapon_id_seq OWNED BY public.weapons.weapon_id;


--
-- Name: adventurer adventurer_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.adventurer ALTER COLUMN adventurer_id SET DEFAULT nextval('public.adventurer_adventurer_id_seq'::regclass);


--
-- Name: monsters monster_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.monsters ALTER COLUMN monster_id SET DEFAULT nextval('public.monsters_monster_id_seq'::regclass);


--
-- Name: weapons weapon_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weapons ALTER COLUMN weapon_id SET DEFAULT nextval('public.weapons_weapon_id_seq'::regclass);


--
-- Data for Name: adventurer; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.adventurer (adventurer_id, adventurer_hp, adventurer_class, adventurer_password, max_health, adventurer_name, weapon_id, basic_potions, max_potions, defeated_monsters, adventurer_level, adventurer_exp, crit_chance) FROM stdin;
10	90	Basic	1	90	a	1	10	5	0	1	0	0.4
58	15933	Mage	2	16620	b	20	280	1084	20	1653	590	0.4
60	150	Warrior	54472623	150	testWarrior	1	10	5	0	1	0	0.4
61	100	Mage	76992037	100	testMage	11	15	10	0	1	0	0.4
62	100	Assassin	8236365	100	testAssassin	21	10	5	0	1	0	0.7
50	100	Mage	18	100	r	1	15	10	0	1	0	0.4
31	80	Warrior	3	80	c	1	10	5	0	1	0	0.4
52	36	Assassin	7	100	g	1	13	20	1	1	25	0.7
35	150	Assassin	4	150	d	1	10	5	0	1	0	0.4
37	150	Assassin	5	150	e	1	10	5	0	1	0	0.4
51	63	Assassin	8	100	h	1	20	50	4	1	80	0.7
55	100	Assassin	10	100	j	21	10	5	0	1	0	0.7
\.


--
-- Data for Name: monsters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.monsters (monster_id, monster_name, monster_damage, monster_hp, monster_drop_basic, monster_drop_max, monster_drop_exp) FROM stdin;
1	Goblin	5	30	2	10	15
2	Orc	12	80	5	25	50
3	Troll	25	200	15	60	150
4	Dragon	50	500	50	200	500
5	Wolf	8	45	3	15	25
6	Skeleton	7	40	3	12	20
7	Vampire	30	150	20	80	200
8	Slime	3	60	1	5	10
9	Minotaur	35	300	25	100	300
10	Phoenix	40	400	40	150	400
\.


--
-- Data for Name: weapons; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.weapons (weapon_id, weapon_name, weapon_damage, weapon_level_requirement, weapon_class) FROM stdin;
1	Rusty Sword	8	1	Warrior
2	Iron Broadsword	18	10	Warrior
3	Steel Claymore	28	20	Warrior
4	Knight's Longsword	42	30	Warrior
5	Titanium Greatsword	62	40	Warrior
6	Bronze Axe	12	5	Warrior
7	Tempered Flail	22	15	Warrior
8	Executioner's Axe	35	25	Warrior
9	Legionnaire's Halberd	50	35	Warrior
10	Colossus Hammer	70	45	Warrior
11	Apprentice's Wand	4	1	Mage
12	Oak Staff	12	10	Mage
13	Crystal Focus	22	20	Mage
14	Arcane Scepter	35	30	Mage
15	Elder Staff	55	40	Mage
16	Willow Branch	6	5	Mage
17	Runed Orb	16	15	Mage
18	Frostfire Rod	28	25	Mage
19	Celestial Catalyst	42	35	Mage
20	Phoenix Flame Staff	60	45	Mage
21	Chipped Dagger	6	1	Assassin
22	Serrated Knife	16	10	Assassin
23	Shadow Blade	26	20	Assassin
24	Viper's Fang	38	30	Assassin
25	Ebony Shard	58	40	Assassin
26	Bone Shiv	10	5	Assassin
27	Twin Hookblades	20	15	Assassin
28	Assassin's Kris	32	25	Assassin
29	Nightfall Daggers	46	35	Assassin
30	Death's Whisper	68	45	Assassin
\.


--
-- Name: adventurer_adventurer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.adventurer_adventurer_id_seq', 62, true);


--
-- Name: monsters_monster_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.monsters_monster_id_seq', 10, true);


--
-- Name: weapons_weapon_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.weapons_weapon_id_seq', 30, true);


--
-- Name: adventurer adventurer_adventurer_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.adventurer
    ADD CONSTRAINT adventurer_adventurer_name_key UNIQUE (adventurer_name);


--
-- Name: adventurer adventurer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.adventurer
    ADD CONSTRAINT adventurer_pkey PRIMARY KEY (adventurer_id);


--
-- Name: monsters monsters_monster_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.monsters
    ADD CONSTRAINT monsters_monster_name_key UNIQUE (monster_name);


--
-- Name: monsters monsters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.monsters
    ADD CONSTRAINT monsters_pkey PRIMARY KEY (monster_id);


--
-- Name: weapons weapons_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.weapons
    ADD CONSTRAINT weapons_pkey PRIMARY KEY (weapon_id);


--
-- PostgreSQL database dump complete
--

