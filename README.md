# Archive2git

Convert folder of releases to a git repository.

Useful for consulting engagements, to transition clients using folder-based version control to git.

## Requirements

- Java 11+.

## Installation

Download the latest release and use it via `./archive2git ...`, or add it to your PATH for long-term use.

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

- `config`: defaults to 'archive2git.json' in the `path`.
- `path`: defaults to current directory.

## Config File Options

- `committer`: (optional) name of committer, defaults to "archive2git".
- `releases`: array of releases to generate:
    - `path`: path to folder.
    - `title`: commit title.
    - `at`: (optional) date/time of commit, defaults to now.
    - `committer` (optional) name of committer, overrides `committer` global option.

## License

Archive2git is open-sourced software licensed under the [Apache 2.0 license](LICENSE).
