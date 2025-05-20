-- init.sql
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
    BIGINT,
    route_image_url
    TEXT
);

INSERT INTO tours (name, description, from_location, to_location, transport_type, distance, estimated_time,
                   route_image_url)
VALUES ('City Highlights',
        'A guided bike tour through the historic city center.',
        'Old Town',
        'City Park',
        'bike',
        12.5,
        5400,
        'https://example.com/city-tour.png'),
       ('Mountain Hike',
        'A challenging hike up the local mountain trail.',
        'Trailhead',
        'Summit',
        'hike',
        8.0,
        14400,
        'https://example.com/mountain-hike.png'),
       ('Beach Vacation',
        'A relaxing walk along the coastal boardwalk.',
        'Pier',
        'Lighthouse',
        'walk',
        5.0,
        7200,
        'https://example.com/beach-vacation.png');