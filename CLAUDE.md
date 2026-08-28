# Project notes for Claude

- Never use the `find` command via Bash on this machine - it does not reliably terminate on this
  OS/shell setup and leaves orphaned processes running indefinitely (observed multiple `find`
  processes still running 90+ minutes after being launched). Use the `Glob` tool for file-pattern
  searches and the `Grep` tool for content searches instead - they cover the same use cases.
