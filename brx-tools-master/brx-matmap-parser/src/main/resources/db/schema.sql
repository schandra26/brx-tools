-- BRx Tools initial DB schema reference
-- This file is a reference for the tables used by JPA entities in the project.

CREATE TABLE IF NOT EXISTS matmap_mappings (
  id SERIAL PRIMARY KEY,
  source_field TEXT,
  target_field TEXT,
  non_brx_enumeration_metadata TEXT,
  filename VARCHAR(255),
  last_committer VARCHAR(255),
  last_committed_at TIMESTAMP,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS openapi_mappings (
  id SERIAL PRIMARY KEY,
  source_field TEXT,
  target_field TEXT,
  non_brx_enumeration_metadata TEXT,
  filename VARCHAR(255),
  last_committer VARCHAR(255),
  last_committed_at TIMESTAMP,
  created_at TIMESTAMP
);

-- Repository sources (source code repositories containing matmap files)
CREATE TABLE IF NOT EXISTS repository_sources (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  url TEXT,
  type VARCHAR(100),
  enabled BOOLEAN DEFAULT TRUE,
  last_scanned_at TIMESTAMP
);

-- Additional tables (scans, logical model versions, etc.) would follow similar structure.
