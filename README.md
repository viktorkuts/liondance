# LionDance

LVH Lion Dance Team repo

### Please read the [Collaborating](#collab) section before starting!

## How to use - Docker
Run docker-compose and it shall build everything for you.  
You can work on the frontend and the container will automatically reload the React application.  
However you will still need to re-run docker-compose everytime you change something in the backend.

```
docker compose up --build
```

## How to use - Manual
If you prefer, you can also run manually, given that you have Mongo installed and set up properly.

### Frontend
We are going to use yarn as the package manager.
Here is the procedure to install yarn:
```
npm install --global yarn
```
Whenever you are working on the frontend, make sure you are inside the frontend folder
```
cd liondance-fe
```
Afterwards, run yarn to install/update dependencies
```
yarn
```

### Backend
Straight forward, just build & run using gradle or use IntelliJ.

# <a name="collab"></a> Collaborating

### Commit Message Format
It would be preferable to use the following format for commit messages:
```
feat: sample description
```
Here's an example of types:
- ci: Changes to CI
- docs: Documentation
- feat: A new feature
- fix: A bug fix
- refac: Refactoring
- test: Adding or updating tests
- chore: Something that had to be done

### Branch Naming
The name of the branch must be of the following format:
```
feat/student-registration
```

### Pull Requests
Pull Requests should follow this format:
```
feat(1234): sample description
```

No need for LLDT tag or anything, it's redundant.

For Pull Request commit message, use the following:
```
feat: sample description (#123)
```