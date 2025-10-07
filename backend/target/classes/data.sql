-- Seed roles 
INSERT INTO roles (id, name) VALUES
    (1, 'PRODUCT_OWNER'),
    (2, 'END_USER')
ON CONFLICT (name) DO NOTHING;

-- Seed users
INSERT INTO users (id, email, password_hash, display_name) VALUES
    ('2f4b4c01-d7f3-4f1b-8eb6-01e6b0fbd7a1', 'owner1@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5p/lqYp7Di1pOAGz0zzP6z1ZZh.GCq', 'Owner One'),
    ('6c0f9e20-64b1-4b46-9fa4-d9af1ed8ec23', 'owner2@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5p/lqYp7Di1pOAGz0zzP6z1ZZh.GCq', 'Owner Two'),
    ('bc5147cb-0c1b-495b-9d72-2f8f3f017f3f', 'user1@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5p/lqYp7Di1pOAGz0zzP6z1ZZh.GCq', 'User One'),
    ('d1bb6a5b-318b-4741-b26d-77c6f13fa2a6', 'user2@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5p/lqYp7Di1pOAGz0zzP6z1ZZh.GCq', 'User Two')
ON CONFLICT (email) DO NOTHING;

-- Seed user roles
INSERT INTO user_roles (user_id, role_id)
SELECT mappings.user_id, mappings.role_id
FROM (
    SELECT '2f4b4c01-d7f3-4f1b-8eb6-01e6b0fbd7a1'::uuid AS user_id,
           (SELECT id FROM roles WHERE name = 'PRODUCT_OWNER') AS role_id
    UNION ALL
    SELECT '6c0f9e20-64b1-4b46-9fa4-d9af1ed8ec23'::uuid,
           (SELECT id FROM roles WHERE name = 'PRODUCT_OWNER')
    UNION ALL
    SELECT 'bc5147cb-0c1b-495b-9d72-2f8f3f017f3f'::uuid,
           (SELECT id FROM roles WHERE name = 'END_USER')
    UNION ALL
    SELECT 'd1bb6a5b-318b-4741-b26d-77c6f13fa2a6'::uuid,
           (SELECT id FROM roles WHERE name = 'END_USER')
) AS mappings
WHERE mappings.role_id IS NOT NULL
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Seed products
INSERT INTO products (id, owner_id, name, description) VALUES
    ('a59f5ac4-5134-46d5-8818-2c6fa0180f2a', '2f4b4c01-d7f3-4f1b-8eb6-01e6b0fbd7a1', 'Analytics Dashboard', 'Self-service analytics dashboard for operational teams.'),
    ('b92f6b52-4aee-40bc-8ad6-7a0b8c0d4c78', '2f4b4c01-d7f3-4f1b-8eb6-01e6b0fbd7a1', 'IoT Device Hub', 'Centralized hub for managing and monitoring IoT sensors.'),
    ('cbbbd757-bd23-49f2-87fa-1e1796c8bda1', '6c0f9e20-64b1-4b46-9fa4-d9af1ed8ec23', 'Subscription Manager', 'Billing and subscription management platform for SaaS businesses.')
ON CONFLICT (id) DO NOTHING;

-- Seed reviews
INSERT INTO reviews (id, product_id, reviewer_id, rating, comment) VALUES
    ('f1c2b91d-4032-4c6e-8f88-2efd9b632e30', 'a59f5ac4-5134-46d5-8818-2c6fa0180f2a', 'bc5147cb-0c1b-495b-9d72-2f8f3f017f3f', 9, 'Impressive analytics and fast dashboards.'),
    ('6d0d9b13-4a09-4cf7-a0f4-5aa52b1a39fc', 'b92f6b52-4aee-40bc-8ad6-7a0b8c0d4c78', 'd1bb6a5b-318b-4741-b26d-77c6f13fa2a6', 8, 'Love the hardware integration, setup docs could improve.'),
    ('8d5e9305-e0f6-4a33-b301-58e219fc7009', 'cbbbd757-bd23-49f2-87fa-1e1796c8bda1', 'bc5147cb-0c1b-495b-9d72-2f8f3f017f3f', 7, 'Solid billing workflows but reporting is limited.')
ON CONFLICT (id) DO NOTHING;
