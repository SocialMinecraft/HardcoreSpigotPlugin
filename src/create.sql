CREATE TABLE players (
     player_uuid uuid PRIMARY KEY,
     joined      TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
     last_seen   TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
     name        VARCHAR(17) NOT NULL DEFAULT 'Unknown',
     playtime    INT NOT NULL DEFAULT 0,
     wallet      INT NOT NULL DEFAULT 0
);

CREATE TYPE event_type AS ENUM ('died', 'offense', 'bought', 'transaction', 'revived', 'daily');

CREATE TABLE events (
    id          BIGSERIAL PRIMARY KEY,
    stamp       TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
    player_uuid UUID NOT NULL,
    playtime    INT NOT NULL,
    type        event_type NOT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY (player_uuid) REFERENCES players(player_uuid)
);

CREATE TABLE powers (
    player_uuid UUID NOT NULL,
    effect      VARCHAR(30) NOT NULL,
    level       INT NOT NULL,

    FOREIGN KEY (player_uuid) REFERENCES players(player_uuid),
    PRIMARY KEY (player_uuid, effect)
);