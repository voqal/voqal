The Voqal Library is organized by application and service, with each containing a set of contexts
and tools that can be used to interact with the application or service. Contexts provide information about the current
state of the application or service, while tools provide the ability to perform actions within the application or
service.

- [Applications](#applications)
    - [Chrome](#chrome)
    - [Visual Studio Code](#visual-studio-code)
- [Services](#services)
    - [Gmail API](#gmail-api)
- [Websites](#websites)
    - [Gmail](#gmail)
    - [YouTube](#youtube)

# Voqal (Default)

### Tools

- [Answer Question](./voqal/tools/answer_question) - Answer a question
- [Ignore](./voqal/tools/ignore) - Ignore a transcription

# Applications

## Chrome

### Tools

- [Create Tab](./chrome/tools/create_tab) - Create a new tab in the browser
- [Make Tab Active](./chrome/tools/make_tab_active) - Make a tab active in the browser

## Visual Studio Code

### Context

- [Active Text Editor](./vscode/context/active_text_editor) - The active text editor in Visual Studio Code
- [Open Files](./vscode/context/open_files) - The files that are currently open in Visual Studio Code
- [Project File Tree](./vscode/context/project_file_tree) - The file tree of the current project in Visual Studio Code
- [Project Root](./vscode/context/project_root) - The root directory of the current project in Visual Studio Code
- [Workspace Files](./vscode/context/workspace_files) - The files in the current workspace in Visual Studio Code

### Tools

- [Cancel](./vscode/tools/cancel) - Cancel the current operation
- [Close File](./vscode/tools/close_file) - Close a file in Visual Studio Code
- [Edit Text](./vscode/tools/edit_text) - Edit the text in the active text editor in Visual Studio Code
- [Goto Line](./vscode/tools/goto_line) - Go to a specific line in the active text editor in Visual Studio Code
- [Looks Good](./vscode/tools/looks_good) - Mark a transcription as "looks good"
- [Open File](./vscode/tools/open_file) - Open a file in Visual Studio Code
- [Toggle Edit Mode](./vscode/tools/toggle_edit_mode) - Toggle edit mode in the active text editor in Visual Studio Code

# Services

## Gmail API

### Tools

- [Add Label](./gmail_api/tools/add_label) - Add a label to an email
- [Make Draft](./gmail_api/tools/make_draft) - Create a draft email

# Websites

## Gmail

### Context

- [Displayed Email](./gmail/context/displayed_email) - The email that is currently displayed in Gmail
- [Is Inside Email](./gmail/context/is_inside_email) - Whether the user is inside an email in Gmail
- [List Emails](./gmail/context/list_emails) - The list of emails in Gmail
- [User Info](./gmail/context/user_info) - Information about the user in Gmail

### Tools

- [Back To Inbox](./gmail/tools/back_to_inbox) - Go back to the inbox in Gmail
- [Draft Email Reply](./gmail/tools/draft_email_reply) - Draft a reply to an email in Gmail
- [Mark Selected Emails](./gmail/tools/mark_selected_emails) - Mark the selected emails in Gmail
- [Read Email](./gmail/tools/read_email) - Read an email in Gmail
- [Select Emails](./gmail/tools/select_emails) - Select emails in Gmail

## YouTube

### Context

- [Current Time](./youtube/context/current_time) - The time at which the video is currently playing
- [Video Duration](./youtube/context/video_duration) - The total duration of the video

### Tools

- [Next Video](./youtube/tools/next_video) - Play the next video in the playlist
- [Previous Video](./youtube/tools/previous_video) - Play the previous video in the playlist
- [Search](./youtube/tools/search) - Search for a video
- [Seek Video](./youtube/tools/seek_video) - Move to a specific time in the video
