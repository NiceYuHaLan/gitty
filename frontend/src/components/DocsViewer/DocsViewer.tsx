import { useState } from 'react';
import { Modal } from '../Modal/Modal';
import { CommitList } from '../CommitList/CommitList';
import type { Project } from '../../api/projectsApi';
import './DocsViewer.css';

interface DocsViewerProps {
  project: Project | null;
  isOpen: boolean;
  onClose: () => void;
}

export function DocsViewer({ project, isOpen, onClose }: DocsViewerProps) {
  const [activeTab, setActiveTab] = useState<'docs' | 'commits'>('docs');

  if (!project) return null;

  const documentation = project.documentation || "Документация пока пуста...";

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Проект: ${project.name}`}>
      <div className="docs-viewer-container">
        <div className="docs-header">
          <div className="tabs">
            <button
              className={`tab ${activeTab === 'docs' ? 'active' : ''}`}
              onClick={() => setActiveTab('docs')}
            >
              Документация
            </button>
            <button
              className={`tab ${activeTab === 'commits' ? 'active' : ''}`}
              onClick={() => setActiveTab('commits')}
            >
              Коммиты и AI-анализ
            </button>
          </div>
          {project.repoUrl && (
            <a href={project.repoUrl} target="_blank" rel="noopener noreferrer" className="docs-repo-link">
              Репозиторий
            </a>
          )}
        </div>

        <div className="docs-content">
          {activeTab === 'docs' && (
            <pre className="docs-text">{documentation}</pre>
          )}
          {activeTab === 'commits' && (
            <CommitList projectId={project.id} />
          )}
        </div>

        <div className="docs-footer">
          <button className="btn-close-docs" onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </Modal>
  );
}