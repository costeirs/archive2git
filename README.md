# Archive2git

Convert folder of releases to a git repository.

Useful for consulting engagements, to transition clients using folder-based version control to git.

## Requirements

- Java 8+.

## Usage

### Init

Scans a given directory and generates a configuration file.

```shell
archive2git init [--committer] [path]
```
- `committer`: name which will appear on commit messages. Defaults to "archive2git".
- `path`: defaults to current directory.

### Convert

```shell
archive2git convert [--config] [path]
```

- `config`: defaults to 'archive2git.json'.
- `path`: defaults to current directory.
