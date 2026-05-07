import './Dashboard.css';
import { TopBar } from '../../components/TopBar/TopBar';
import { ProjectCard } from '../../components/ProjectCard/ProjectCard';
import { Modal } from '../../components/Modal/Modal';
import { ProjectForm } from '../../components/ProjectForm/ProjectForm';
import { DocsViewer } from '../../components/DocsViewer/DocsViewer';
import { ProfileModal } from '../ProfileModal/ProfileModal';
import { projectsApi } from '../../api/projectsApi';
import type { Project, CreateProjectRequest } from '../../api/projectsApi';
import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';

export function Dashboard() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false);

  const [isDocsOpen, setIsDocsOpen] = useState(false);
  const [docsProject, setDocsProject] = useState<Project | null>(null);

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      setLoading(true);
      const data = await projectsApi.getAll();
      setProjects(data);
    } catch (error) {
      console.error('Ошибка загрузки проектов:', error);
      toast.error('Не удалось загрузить проекты');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenAddModal = () => {
    setSelectedProject(null);
    setIsEditMode(false);
    setIsModalOpen(true);
  };

  const handleProjectClick = (project: Project) => {
    setSelectedProject(project);
    setIsEditMode(false);
    setIsModalOpen(true);
  };

  const handleDocsClick = (project: Project) => {
    setDocsProject(project);
    setIsDocsOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedProject(null);
    setIsEditMode(false);
  };

  const handleOpenProfile = () => setIsProfileModalOpen(true);
  const handleCloseProfile = () => setIsProfileModalOpen(false);
  const handleEditClick = () => setIsEditMode(true);
  const handleDeleteClick = () => setIsDeleteConfirmOpen(true);
  const handleCloseDeleteConfirm = () => setIsDeleteConfirmOpen(false);

  const handleConfirmDelete = async () => {
    if (!selectedProject) return;
    try {
      await projectsApi.delete(selectedProject.id);
      toast.success('Проект успешно удалён');
      handleCloseDeleteConfirm();
      handleCloseModal();
      await loadProjects();
    } catch (error) {
      toast.error('Не удалось удалить проект');
    }
  };

  const handleSubmitProject = async (data: CreateProjectRequest) => {
    try {
      if (isEditMode && selectedProject) {
        await projectsApi.update(selectedProject.id, data);
        toast.success('Проект успешно обновлён');
      } else {
        await projectsApi.create(data);
        toast.success('Проект успешно создан');
      }
      handleCloseModal();
      await loadProjects();
    } catch (error) {
      console.error('Ошибка сохранения проекта:', error);
      toast.error('Не удалось сохранить проект');
    }
  };

  if (loading) {
    return (
      <div className="dashboard">
        <TopBar onAddProject={handleOpenAddModal} onOpenProfile={handleOpenProfile} />
        <div className="loading">Загрузка...</div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <TopBar onAddProject={handleOpenAddModal} onOpenProfile={handleOpenProfile} />
      
      <main className="dashboard-main">
        <h2 className="dashboard-title">Мои проекты</h2>
        
        <div className="projects-grid">
          {projects.map((project) => (
            <ProjectCard
              key={project.id}
              project={project}
              onClick={handleProjectClick}
              onDocsClick={handleDocsClick}
            />
          ))}
          
          <div className="project-card add-new-card" onClick={handleOpenAddModal}>
            <div className="add-new-content">
              <span className="add-icon">+</span>
              <p>Добавить проект</p>
            </div>
          </div>
        </div>
      </main>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={isEditMode ? 'Редактировать проект' : (selectedProject ? selectedProject.name : 'Новый проект')}
      >
        {isEditMode ? (
          <ProjectForm
            initialData={selectedProject ? {
              name: selectedProject.name,
              description: selectedProject.description,
              repoUrl: selectedProject.repoUrl,
            } : undefined}
            onSubmit={handleSubmitProject}
            onCancel={handleCloseModal}
            submitLabel="Сохранить изменения"
          />
        ) : selectedProject ? (
          <div className="project-details">
             <div className="details-info">
               <h3>{selectedProject.name}</h3>
               {selectedProject.repoUrl && (
                 <div className="details-repo">
                   <a href={selectedProject.repoUrl} target="_blank" rel="noopener noreferrer">
                     {selectedProject.repoUrl}
                   </a>
                 </div>
               )}
               <div className="details-description">
                 <h4>Описание</h4>
                 <p>{selectedProject.description || "Нет описания"}</p>
               </div>
               <div className="details-actions">
                 <button className="btn-edit" onClick={handleEditClick}>Редактировать</button>
                 <button className="btn-delete" onClick={handleDeleteClick}>Удалить</button>
               </div>
             </div>
          </div>
        ) : (
          <ProjectForm onSubmit={handleSubmitProject} onCancel={handleCloseModal} submitLabel="Создать проект" />
        )}
      </Modal>

      <DocsViewer 
        project={docsProject} 
        isOpen={isDocsOpen} 
        onClose={() => setIsDocsOpen(false)} 
      />

      <ProfileModal isOpen={isProfileModalOpen} onClose={handleCloseProfile} />

      <Modal isOpen={isDeleteConfirmOpen} onClose={handleCloseDeleteConfirm} title="Удалить проект?">
        <div className="delete-confirm">
          <p>Вы уверены, что хотите удалить проект <strong>"{selectedProject?.name}"</strong>?</p>
          <div className="delete-confirm-actions">
            <button className="btn-cancel-delete" onClick={handleCloseDeleteConfirm}>Отмена</button>
            <button className="btn-confirm-delete" onClick={handleConfirmDelete}>Удалить</button>
          </div>
        </div>
      </Modal>
    </div>
  );
}