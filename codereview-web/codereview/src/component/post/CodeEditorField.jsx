import Editor from '@monaco-editor/react';
import { MONACO_LANGUAGE_MAP } from '../../constants/postOptions';

const MAX_LENGTH = 10000;

export default function CodeEditorField({ value, onChange, language, error }) {
  const monacoLanguage = MONACO_LANGUAGE_MAP[language] ?? 'plaintext';

  return (
    <div className="flex flex-col gap-1.5">
      <div className="flex items-center justify-between">
        <label className="text-sm font-medium text-text-muted">코드</label>
        <span
          className={`font-mono text-xs ${
            value.length > MAX_LENGTH ? 'text-red-300' : 'text-text-faint'
          }`}
        >
          {value.length.toLocaleString()} / {MAX_LENGTH.toLocaleString()}자
        </span>
      </div>
      <div className={`monaco-shell ${error ? 'border-danger/60' : ''}`}>
        <Editor
          height="420px"
          theme="vs-dark"
          language={monacoLanguage}
          value={value}
          onChange={(v) => onChange(v ?? '')}
          options={{
            fontSize: 13,
            fontFamily: 'JetBrains Mono, monospace',
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            wordWrap: 'on',
            padding: { top: 12 },
          }}
        />
      </div>
      {error && <p className="text-xs text-red-300">{error}</p>}
    </div>
  );
}
