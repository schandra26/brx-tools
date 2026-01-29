## Development environment setup

### Gitlab Access Token Setup

The app uses a `GITLAB_ACCESS_TOKEN` environment variable which you need to set up on your machine.

**Generating a Gitlab Access Token:**
1. Go to `https://git.devops.broadridge.net/-/user_settings/personal_access_tokens`
2. Click on 'Add new token'
3. Choose a name for your token - this can be anything you want, but in order to keep it simple you can set it as `MatmapFinderToken`
4. Optionally set an `Expiration Date`
5. Choose the following `Scopes`: `api, read_api, read_repository`
6. After clicking `Create Personal Access Token` the token will be shown just once. Make sure to save it somewhere until you set up the environment variable (see below). If you lose your token you will need to delete the old one and create a new one.

**Setting the env variable - Windows:**
1. Open Search and find `Edit the system environment variable`
2. Click on `Environment Variables...`
3. Under the `System Variables` click `New`
4. Enter `Variable Name`: `GITLAB_ACCESS_TOKEN`
5. Enter `Variable Value`: `<YOUR_GITLAB_ACCESS_TOKEN>`
6. Click `OK` and restart the system

**Setting the env variable - macOS:**
1. Open a terminal window
2. Edit the zsh exports: `vi ~/.zshrc`
3. Add the following line: `export GITLAB_ACCESS_TOKEN=<YOUR_GITLAB_ACCESS_TOKEN>`
4. Run `source ~/.zshrc` to reload the contents in the terminal


### PostgreSQL Setup using Docker

1. Make sure you have Docker Desktop installed on your machine.
2. Get the docker image: `docker pull postgres:16.4`
3. Run the docker image: 
   ```
   docker run --name brx-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres:16.4
   ```

### Logical Model file loading strategy configuration
The configuration is done through `application.yml` file
#### 1. Gitlab Repository

- `brx.logical.model.external.gitlab.url` : the url of the repository project to fetch the logical model file
- `brx.logical.model.external.gitlab.location` : the location where the logical model file is within that repository project

#### 2. Local

- `brx.logical.model.local.location` : the location within the environment to load the local excel file from

#### Common

- `brx.logical.model.filename` : the name of the logical model file (it is used by both Gitlab Repository and Local strategies to compose the full path of the file -> using the location property that each of them has)

#### Strategy

- `brx.logical.model.loading.strategy` : used to configure the strategy that is going to be used in the project
- the values that it has for the 2 strategies that we have in place are : 
  - `gitlabRepository`
  - `local`
- the default strategy to be configured for now is `gitlabRepository`

## Building and Running

Clone the git repository using `git clone git@git.devops.broadridge.net:GTO-FTB/EP/BRx/brx-tools.git` and go into
the `brx-tools/brx-matmap-parser` directory.

The application can be built using Maven: `mvn clean install`

In order to run the application you can either run the `BrxMappingsParserApplication.class` or `mvn spring-boot:run` in your command prompt/terminal window

After building the app, you can run also the jar like this: `java -DGITLAB_ACCESS_TOKEN=xxxxxxxx -jar target/brx-matmap-parser-0.0.1-SNAPSHOT.jar`

Access the mappings page http://localhost:8081/mappings

Running the vulnerabilities maven plugin check: `mvn verify -Pdependency-check -DskipTests`