# Archive2git

Convert folder of releases to a git repository.

Useful for consulting engagements, to transition clients using folder-based version control to git.

## Requirements

- Java 11+.

## Installation

Download the latest release and use it via `java -jar archive2git.jar ...`.

## Usage

### Init

Scans a given directory and generates a configuration file.

```shell
init [--committer] [path]
```
- `committer`: name which will appear on commit messages. Defaults to "archive2git".
- `path`: defaults to current directory.

### Convert

```shell
convert [--config] [path]
```

- `config`: defaults to 'archive2git.json'.
- `path`: defaults to current directory.

## Config File

- `committer`: (optional) name of committer, defaults to "archive2git".
- `releases`: array of releases to generate:
    - `path`: path to folder.
    - `title`: commit title.
    - `at`: (optional) date/time of commit, defaults to now.
    - `committer` (optional) name of committer, overrides `committer` global option.
