import './ProjectFrom.css';
import { useState } from 'react';
import type { CreateProjectRequest } from '../../api/projectsApi';

interface ProjectFormProps {
  initialData?: {
    name: string;
    description: string | null;
    repoUrl?: string;
  };
  onSubmit: (data: CreateProjectRequest) => void;
  onCancel: () => void;
  submitLabel?: string;
}

const MAX_DESCRIPTION_LENGTH = 500;

export function ProjectForm({
  initialData,
  onSubmit,
  onCancel,
  submitLabel = 'Сохранить'
}: ProjectFormProps) {
  const [formData, setFormData] = useState({
    name: initialData?.name || '',
    description: initialData?.description || '',
    repoUrl: initialData?.repoUrl || '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      name: formData.name,
      description: formData.description || undefined,
      repoUrl: formData.repoUrl || undefined,
    });
  };

  const descriptionLength = formData.description.length;
  const isDescriptionTooLong = descriptionLength > MAX_DESCRIPTION_LENGTH;

  return (
    <form onSubmit={handleSubmit} className="project-form">
      <div className="form-group">
        <label htmlFor="name">Название проекта *</label>
        <input
          type="text"
          id="name"
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          placeholder="Например: My Awesome Project"
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="repoUrl">Ссылка на проект</label>
        <input
          type="url"
          id="repoUrl"
          value={formData.repoUrl}
          onChange={(e) => setFormData({ ...formData, repoUrl: e.target.value })}
          placeholder="https://github.com/user/project"
        />
      </div>

      <div className="form-group">
        <label htmlFor="description">Описание</label>
        <textarea
          id="description"
          value={formData.description}
          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          placeholder="Краткое описание проекта..."
          rows={4}
          maxLength={MAX_DESCRIPTION_LENGTH}
        />
        <div className={`char-counter ${isDescriptionTooLong ? 'error' : ''}`}>
          {descriptionLength} / {MAX_DESCRIPTION_LENGTH}
        </div>
      </div>

      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel}>
          Отмена
        </button>
        <button 
          type="submit" 
          className="btn-submit"
          disabled={isDescriptionTooLong}
        >
          {submitLabel}
        </button>
      </div>
    </form>
  );
}