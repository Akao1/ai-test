import markdown, os

def md_to_html(md_path, html_path):
    with open(md_path, 'r', encoding='utf-8') as f:
        md_content = f.read()
    body = markdown.markdown(md_content, extensions=['tables', 'fenced_code'])
    html = f'''<!DOCTYPE html>
<html lang="zh-CN">
<head><meta charset="utf-8">
<style>
  body {{ font-family: 'Microsoft YaHei','SimHei',sans-serif; padding:40px; line-height:1.8; font-size:13px; }}
  h1 {{ color:#1a1a2e; font-size:22px; text-align:center; border-bottom:3px solid #e94560; padding-bottom:12px; }}
  h2 {{ color:#16213e; font-size:17px; margin-top:22px; border-left:4px solid #e94560; padding-left:10px; }}
  table {{ border-collapse:collapse; width:100%; margin:12px 0; }}
  th,td {{ border:1px solid #ccc; padding:6px 10px; text-align:left; }}
  th {{ background:#f0f0f0; }}
  tr:nth-child(even) {{ background:#fafafa; }}
  ul,ol {{ margin:6px 0; padding-left:22px; }}
  li {{ margin:3px 0; }}
</style></head>
<body>{body}</body></html>'''
    with open(html_path, 'w', encoding='utf-8') as f:
        f.write(html)

md_to_html(r'D:\my\模板一.md', r'D:\my\模板一.html')
md_to_html(r'D:\my\模板二.md', r'D:\my\模板二.html')
print("HTML files generated")
