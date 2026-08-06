// 카테고리/언어는 목록 조회 시 정확히 일치(equals)하는 값으로 필터링되므로
// 작성 폼과 필터 UI가 반드시 동일한 값 목록을 참조해야 한다.

export const CATEGORIES = [
  '알고리즘',
  '리팩토링',
  '버그수정',
  '신규기능',
  '코드스타일',
  '성능최적화',
  '기타',
];

export const LANGUAGES = [
  'Java',
  'JavaScript',
  'TypeScript',
  'Python',
  'C',
  'C++',
  'C#',
  'Go',
  'Kotlin',
  'SQL',
  'HTML/CSS',
  'Other',
];

// Monaco 에디터 language prop과 매핑 (표시용 언어명 -> monaco language id)
export const MONACO_LANGUAGE_MAP = {
  Java: 'java',
  JavaScript: 'javascript',
  TypeScript: 'typescript',
  Python: 'python',
  C: 'c',
  'C++': 'cpp',
  'C#': 'csharp',
  Go: 'go',
  Kotlin: 'kotlin',
  SQL: 'sql',
  'HTML/CSS': 'html',
  Other: 'plaintext',
};
