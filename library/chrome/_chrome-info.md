## Tabs Open

{% for tab in integration.chrome.tabs %}
- {{ tab.url }}
  - Id: {{ tab.id }}
  - Title: {{ tab.title }}
  - Active: {{ tab.active }}
{% endfor %}