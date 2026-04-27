import './DocsViewer.css';
import { Modal } from '../Modal/Modal';
import type { Project } from '../../api/projectsApi';

interface DocsViewerProps {
  project: Project | null;
  isOpen: boolean;
  onClose: () => void;
}

export function DocsViewer({ project, isOpen, onClose }: DocsViewerProps) {
  if (!project) return null;

  const content = project.documentation || "Документация пока пуста...";

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Документация проекта">
      <div className="docs-viewer-container">
        <div className="docs-header">
          <h3 className="docs-project-name">{project.name}</h3>
          {project.repoUrl && (
            <a 
              href={project.repoUrl} 
              target="_blank" 
              rel="noopener noreferrer" 
              className="docs-repo-link"
            >
              Репозиторий
            </a>
          )}
        </div>

        <div className="docs-content">
          <pre className="docs-text">{content}</pre>
        </div>

        <div className="docs-footer">
          <button className="btn-close-docs" onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </Modal>
  );
}