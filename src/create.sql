CREATE TABLE players (
     player_uuid uuid PRIMARY KEY,
     joined TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
     last_joined TIMESTAMP WITH TIME ZONE, /* Nullable */
     prev_last_joined TIMESTAMP WITH TIME ZONE /* Used for last join return */
);

CREATE TABLE deaths (
    id BIGSERIAL PRIMARY KEY,
    stamp TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
    player_uuid UUID NOT NULL,
    playtime int NOT NULL,
    reason text,
    FOREIGN KEY (player_uuid) REFERENCES players(player_uuid)
);

CREATE TABLE offenses (
    id BIGSERIAL PRIMARY KEY,
    stamp TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
    player_uuid UUID NOT NULL,
    reason text,
    FOREIGN KEY (player_uuid) REFERENCES players(player_uuid)
);

CREATE TABLE extra_lives (
    id BIGSERIAL PRIMARY KEY,
    stamp TIMESTAMP WITH TIME ZONE DEFAULT  current_timestamp NOT NULL,
    player_uuid UUID NOT NULL,
    reason text,
    FOREIGN KEY (player_uuid) REFERENCES players(player_uuid)
);

ALTER TABLE players ADD name VARCHAR(17) NOT NULL DEFAULT 'Unknown'; /* Max user names are 16 characters, but adding an extra." */
ALTER TABLE players ADD playtime INT NOT NULL DEFAULT 0;
ALTER TABLE offenses ADD playtime int NOT NULL DEFAULT 0;
ALTER TABLE extra_lives ADD playtime int NOT NULL DEFAULT 0;
UPDATE players SET playtime = coalesce((SELECT max(deaths.playtime) FROM deaths WHERE players.player_uuid = deaths.player_uuid), 0);