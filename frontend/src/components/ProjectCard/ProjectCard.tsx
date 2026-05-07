import './ProjectCard.css';
import type { Project } from '../../api/projectsApi';

interface ProjectCardProps {
  project: Project;
  onClick?: (project: Project) => void;
  onDocsClick?: (project: Project) => void;
}

export function ProjectCard({ project, onClick, onDocsClick }: ProjectCardProps) {
  return (
    <div className="project-card" onClick={() => onClick?.(project)}>
      <div className="card-content">
        <h3 className="card-title">{project.name}</h3>
        {project.description && (
          <p className="card-description">{project.description}</p>
        )}
        
        <button 
          className="btn-docs" 
          onClick={(e) => {
            e.stopPropagation();
            onDocsClick?.(project);
          }}
        >
          Документация
        </button>
      </div>
    </div>
  );
}