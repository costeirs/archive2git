# Archive2git

Convert folder of releases to a git repository.

Useful for consulting engagements, to transition clients using folder-based version control to git.

## Requirements

- Java 11+.

## Installation

Download the latest release and make it executable (`chmod +x archive2git`). 
Optionally, add it to your PATH for long-term use.

## Usage

### Init

Scans a given directory and generates a configuration file.

```shell
archive2git init <path> [--committer] [--prefix] [--sort]
```
- `path`: defaults to current directory.
- `committer`: name which will appear on commit messages. Defaults to "archive2git".
- `prefix`: (optional) string to prepend to release titles.

> ☞ Review the config file for correct ordering and customize before converting.

### Convert

```shell
archive2git convert <path> [--config]
```

- `path`: defaults to current directory.
- `config`: defaults to "archive2git.json" in the `path`.

## Config File Schema

- `committer`: (optional) name of committer, defaults to "archive2git".
- `releases`: array of releases to convert:
    - `path`: path to folder.
    - `title`: commit title.
    - `at`: (optional) date/time of commit, defaults to now.
    - `committer` (optional) name of committer, overrides `committer` global option.

## License

Archive2git is open-sourced software licensed under the [Apache 2.0 license](LICENSE).
