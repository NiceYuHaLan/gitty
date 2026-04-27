import './ProjectFrom.css';
import { useState, useRef } from 'react';
import type { CreateProjectRequest } from '../../api/projectsApi';

interface ProjectFormProps {
  initialData?: {
    name: string;
    description: string | null;
    imageUrl: string;
    repoUrl?: string; 
  };
  onSubmit: (data: CreateProjectRequest) => void;
  onCancel: () => void;
  submitLabel?: string;
}

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
    image: undefined as File | undefined,
    imagePreview: initialData?.imageUrl,
  });
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleImageChange = (file: File) => {
    if (file) {
      const preview = URL.createObjectURL(file);
      setFormData({ ...formData, image: file, imagePreview: preview });
    }
  };

  const onFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) handleImageChange(file);
  };

  const onDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const onDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const onDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    const file = e.dataTransfer.files?.[0];
    if (file && file.type.startsWith('image/')) {
      handleImageChange(file);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      name: formData.name,
      description: formData.description || undefined,
      repoUrl: formData.repoUrl || undefined,
      image: formData.image,
    });
  };

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

      {}
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
        <label>Изображение проекта</label>
        
        {formData.imagePreview ? (
          <div className="image-preview-container">
            <div className="image-preview">
              <img src={formData.imagePreview} alt="Preview" />
            </div>
            <div className="image-preview-actions">
              <button 
                type="button" 
                className="btn-change-image"
                onClick={() => fileInputRef.current?.click()}
              >
                Заменить
              </button>
              <button 
                type="button" 
                className="btn-remove-image"
                onClick={() => {
                  setFormData({ ...formData, image: undefined, imagePreview: undefined });
                  if (fileInputRef.current) fileInputRef.current.value = '';
                }}
              >
                Удалить
              </button>
            </div>
          </div>
        ) : (
          <div 
            className={`upload-zone ${isDragging ? 'dragging' : ''}`}
            onDragOver={onDragOver}
            onDragLeave={onDragLeave}
            onDrop={onDrop}
            onClick={() => fileInputRef.current?.click()}
          >
            <div className="upload-zone-content">
              <div className="upload-icon-wrapper">
                <svg className="upload-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                  <polyline points="17 8 12 3 7 8" />
                  <line x1="12" y1="3" x2="12" y2="15" />
                </svg>
              </div>
              <p className="upload-text">
                <span className="upload-highlight">Загрузить изображение</span>
              </p>
              <p className="upload-hint">PNG, JPG, GIF до 10MB</p>
            </div>
            <input
              ref={fileInputRef}
              type="file"
              id="image"
              accept="image/*"
              onChange={onFileInputChange}
              style={{ display: 'none' }}
            />
          </div>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="description">Описание</label>
        <textarea
          id="description"
          value={formData.description}
          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          placeholder="Краткое описание проекта..."
          rows={4}
        />
      </div>

      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel}>
          Отмена
        </button>
        <button type="submit" className="btn-submit">
          {submitLabel}
        </button>
      </div>
    </form>
  );
}