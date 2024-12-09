## Computer Info

- Current time: {{ computer.currentTime | date("hh:mm a z") }}
- OS Name: {{ computer.osName }}
- OS Version: {{ computer.osVersion }}
- OS Arch: {{ computer.osArch }}

{% if computer.visibleApplications is not empty %}

## Visible Applications

{% for app in computer.visibleApplications %}

### {{ app.title }}

- ID: {{ app.id }}
- Process Name: {{ app.processName }}
- Foreground: {{ app.foreground }}

{% endfor %}

{% endif %}