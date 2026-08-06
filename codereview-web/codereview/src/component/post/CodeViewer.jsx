import { useMemo } from 'react';
import Editor from '@monaco-editor/react';
import { MONACO_LANGUAGE_MAP } from '../../constants/postOptions';

export default function CodeViewer({ code, language }) {
  const monacoLanguage = MONACO_LANGUAGE_MAP[language] ?? 'plaintext';

  const height = useMemo(() => {
    const lineCount = code.split('\n').length;
    return `${Math.min(600, Math.max(160, lineCount * 19 + 24))}px`;
  }, [code]);

  return (
    <div className="monaco-shell">
      <Editor
        height={height}
        theme="vs-dark"
        language={monacoLanguage}
        value={code}
        options={{
          readOnly: true,
          domReadOnly: true,
          fontSize: 13,
          fontFamily: 'JetBrains Mono, monospace',
          minimap: { enabled: false },
          scrollBeyondLastLine: false,
          wordWrap: 'on',
          padding: { top: 12 },
        }}
      />
    </div>
  );
}
