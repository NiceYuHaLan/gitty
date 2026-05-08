import { useEffect, useState } from 'react';
import { commitsApi } from '../../api/commitsApi';
import type { Commit } from '../../api/commitsApi';

interface CommitListProps {
  projectId: number;
}

export function CommitList({ projectId }: CommitListProps) {
  const [commits, setCommits] = useState<Commit[]>([]);
  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);

  const loadCommits = async () => {
    setLoading(true);
    try {
      const data = await commitsApi.getByProject(projectId);
      setCommits(data);
    } finally {
      setLoading(false);
    }
  };

  const handleSync = async () => {
    setSyncing(true);
    try {
      const result = await commitsApi.sync(projectId);
      console.log(`Synced ${result.synced} commits`);
      await loadCommits();
    } finally {
      setSyncing(false);
    }
  };

  useEffect(() => {
    loadCommits();
  }, [projectId]);

  return (
    <div className="commit-list">
      <div className="commit-list-header">
        <h3>Коммиты</h3>
        <button onClick={handleSync} disabled={syncing}>
          {syncing ? 'Синхронизация...' : 'Синхронизировать'}
        </button>
      </div>
      {loading && <p>Загрузка...</p>}
      <ul>
        {commits.map(commit => (
          <li key={commit.id}>
            <strong>{commit.sha.substring(0, 7)}</strong> – {commit.message}
            <br />
            <small>{commit.authorName} · {new Date(commit.commitDate).toLocaleString()}</small>
            {commit.analysis && (
              <div className="analysis">
                <span>Тональность: {commit.analysis.sentiment}</span>
                <p>{commit.analysis.summary}</p>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}