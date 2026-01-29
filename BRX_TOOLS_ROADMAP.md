# BRx Tools Development Roadmap & Blueprint

## 1. Project Blueprint

**Objective:**
Build a modern, scalable platform for cataloging, governing, and analyzing BRx Logical Model mappings, with advanced UI/UX, robust backend, and extensible architecture.

**Tech Stack:**
- **Backend:** Spring Boot 3.5.6, Spring Data JPA/Hibernate, PostgreSQL
- **Frontend:** React (modular, scalable, modern UX)
- **Storage:** PostgreSQL, Docker Compose for local dev
- **Integration:** gitlab4j-api for matmap file access
- **Parsing:** Enhanced extractor for transformation type, enums, complexity
- **Logical Model:** Excel upload, parsed and versioned in DB
- **CI/CD:** GitLab CI or GitHub Actions, OpenAPI docs
- **Testing:** TDD, unit/integration/E2E tests, real matmap samples
- **Security:** No auth for MVP, JWT/roles in later phases

---

## 2. Living Roadmap & Task List

### Phase 1: Foundation & Infrastructure
- [ ] Initialize Git repository and project structure
- [ ] Set up Docker Compose for PostgreSQL and backend
- [ ] Create base Spring Boot project (REST API, JPA, DB config)
- [ ] Scaffold React frontend (Vite/CRA, folder structure)
- [ ] Add README and contribution guidelines

### Phase 2: Data Model & Backend APIs
- [ ] Design DB schema: repository_sources, matmap_mappings, brx_logical_model_versions, scan_jobs
- [ ] Implement JPA entities and repositories
- [ ] Develop matmap parsing service (EnhancedDirectBRxMappingExtractorStrategy)
- [ ] Implement MappingParserService for ingestion and logical model upload
- [ ] Create REST APIs: QueryController, AdminController
- [ ] Add OpenAPI/Swagger documentation
- [ ] Write unit/integration tests for backend

### Phase 3: Frontend Core Features
- [ ] Build QueryMappings page (table, filters, search, flagging)
- [ ] Implement MappingsTable and MappingDrawer components
- [ ] Add hierarchical filters and advanced search (enumerations, transformation type, etc.)
- [ ] Create Admin page (repository management, scan jobs)
- [ ] Add Pivot view for mapping analysis
- [ ] Connect frontend to backend APIs
- [ ] Write unit/E2E tests for frontend

### Phase 4: Advanced Features & Enhancements
- [ ] Integrate gitlab4j-api for matmap file access
- [ ] Enhance matmap parsing for transformation type, enums, complexity
- [ ] Implement Excel upload and logical model versioning
- [ ] Add mapping flagging and governance workflows
- [ ] Implement bottom drawer for mapping details
- [ ] Add user roles/JWT auth (post-MVP)

### Phase 5: CI/CD, Testing, and Release
- [ ] Set up GitLab CI or GitHub Actions
- [ ] Add test automation and coverage reporting
- [ ] Prepare demo data and real matmap samples
- [ ] Finalize documentation and user guides
- [ ] MVP review, test, and demo
- [ ] Plan for next iteration (feedback, enhancements)

---

## 3. Granular Prompt List (for Coding Agents)

- [ ] Set up Docker Compose for PostgreSQL and backend
- [ ] Scaffold Spring Boot project with REST API and DB config
- [ ] Scaffold React frontend with Vite/CRA
- [ ] Design and implement DB schema
- [ ] Implement JPA entities and repositories
- [ ] Develop matmap parsing service
- [ ] Implement MappingParserService
- [ ] Create REST APIs (QueryController, AdminController)
- [ ] Add OpenAPI/Swagger docs
- [ ] Build QueryMappings page (table, filters, search)
- [ ] Implement MappingsTable and MappingDrawer
- [ ] Add hierarchical filters and advanced search
- [ ] Create Admin page
- [ ] Add Pivot view
- [ ] Connect frontend to backend
- [ ] Integrate gitlab4j-api
- [ ] Enhance matmap parsing
- [ ] Implement Excel upload and versioning
- [ ] Add mapping flagging/governance
- [ ] Implement bottom drawer for details
- [ ] Add user roles/JWT auth
- [ ] Set up CI/CD
- [ ] Add test automation
- [ ] Prepare demo data
- [ ] Finalize docs
- [ ] Review, test, demo MVP

---

**Instructions:**
- Mark each item as In Progress or Done as you work.
- After each iteration, review, test, and demo before moving to the next.
- Update this file as the project evolves.
