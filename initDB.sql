DROP SEQUENCE IF EXISTS tours_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tour_logs_id_seq CASCADE;

DROP TABLE IF EXISTS tour_logs CASCADE;
DROP TABLE IF EXISTS tours CASCADE;

CREATE TABLE IF NOT EXISTS tours
(
    id
    SERIAL
    PRIMARY
    KEY,
    name
    TEXT
    NOT
    NULL,
    description
    TEXT,
    from_location
    TEXT
    NOT
    NULL,
    to_location
    TEXT
    NOT
    NULL,
    transport_type
    TEXT,
    distance
    DOUBLE
    PRECISION,
    estimated_time
    INTERVAL
    NOT NULL,
    route_image_url
    TEXT,
    popularity
    INTEGER,
    child_friendliness
    DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS tour_logs
(
    id
    SERIAL
    PRIMARY
    KEY,
    tour_id
    INTEGER
    NOT
    NULL,
    log_datetime
    TIMESTAMP
    NOT
    NULL,
    comment
    TEXT,
    difficulty
    INTEGER
    NOT
    NULL,
    total_distance
    DOUBLE
    PRECISION
    NOT
    NULL,
    total_time
    INTERVAL
    NOT NULL ,
    rating
    INTEGER
    NOT
    NULL
);
    INSERT INTO tours
(
    name,
    description,
    from_location,
    to_location,
    transport_type,
    distance,
    estimated_time,
    route_image_url,
    popularity,
    child_friendliness
)
    VALUES
(
    'City Highlights',
    'A guided bike tour through the historic city center.',
    'Old Town',
    'City Park',
    'bike',
    12.5,
    INTERVAL '2 hours',
    'https://example.com/city-tour.png',
    5,
    4.5
),
(
    'Mountain Hike',
    'A challenging hike up the local mountain trail.',
    'Trailhead',
    'Summit',
    'hike',
    8.0,
    INTERVAL '4 hours',
    'https://example.com/mountain-hike.png',
    4,
    3.0
),
(
    'Beach Vacation',
    'A relaxing walk along the coastal boardwalk.',
    'Pier',
    'Lighthouse',
    'walk',
    5.0,
    INTERVAL'2 hours',
    'https://example.com/beach-vacation.png',
    3,
    5.0
);


INSERT INTO tour_logs (tour_id, log_datetime, comment, difficulty, total_distance, total_time, rating)
VALUES (1, '2023-10-01 10:00:00', 'Great tour, very informative!', 2, 12.5, INTERVAL '2 hours', 5),
       (2, '2023-10-02 09:30:00', 'Challenging but worth it for the views.', 4, 8.0, INTERVAL '4 hours', 4),
       (3, '2023-10-03 11:00:00', 'Relaxing and beautiful scenery.', 1, 5.0, INTERVAL '2 hours', 5);