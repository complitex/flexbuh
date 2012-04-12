package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.complitex.flexbuh.personnel.entity.*;
import org.complitex.flexbuh.personnel.service.DepartmentBean;
import org.complitex.flexbuh.personnel.web.DepartmentEdit;
import org.complitex.flexbuh.personnel.web.OrganizationEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wickettree.NestedTree;
import wickettree.content.CheckedFolder;
import wickettree.util.InverseSet;
import wickettree.util.ProviderSubset;

import javax.ejb.EJB;
import javax.ejb.ObjectNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pavel Sknar
 *         Date: 21.03.12 15:59
 */
public class DepartmentTreePanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(DepartmentTreePanel.class);

    private static ResourceReference THEME = new DepartmentTreeTheme();

    private NestedTree<Department> tree;

    private Map<Long, IModel<Boolean>> selectedMap = Maps.newHashMap();

    private DepartmentProvider provider = new DepartmentProvider();

    private DepartmentFilter filter = new DepartmentFilter();

    private Set<Department> state = new InverseSet<>(new ProviderSubset<>(provider));

    private Department department;

    @EJB
    private DepartmentBean departmentBean;

    public DepartmentTreePanel(String id, Organization organization) {
        super(id);
        filter.setOrganizationId(organization.getId());
        filter.setSortProperty("name");
        filter.setCount(Integer.MAX_VALUE);
        init();
    }

    public DepartmentTreePanel(String id, Department department) {
        super(id);
        filter.setOrganizationId(department.getOrganization().getId());
        filter.setSortProperty("name");
        filter.setCount(Integer.MAX_VALUE);
        this.department = department;
        init();
    }

    private void init() {

        List<Department> departments = departmentBean.getDepartments(filter);
        log.debug("Departments: {}", departments);
        provider.setRoots(new DepartmentIterator(departments));

        tree = new NestedTree<Department>("tree", provider, newStateModel()) {

            @Override
			protected org.apache.wicket.Component newContentComponent(String id, IModel<Department> model) {

                return /*model.equals(department)?

                        new Folder<Department>(id, tree, model) {

                        }:
                        */
                        new CheckedFolder<Department>(id, tree, model) {

                            //private IModel<Department> selected;

                            @Override
                            protected IModel<Boolean> newCheckBoxModel(final IModel<Department> model) {
                                IModel<Boolean> selectModel = new Model<>(false);
                                if (department != null && department.getId() == null
                                        && model.getObject().equals(department.getMasterDepartment())) {
                                    selectModel.setObject(true);
                                }
                                selectedMap.put(model.getObject().getId(), selectModel);
                                return selectModel;
                            }

                            @Override
                            protected Component newCheckBox(String id, IModel<Department> departmentIModel) {
                                return super.newCheckBox(id, departmentIModel).
                                        setEnabled(!isDisabled(departmentIModel));
                            }

                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                Department department = getModelObject();

                                // search first ancestor with quux not set
                                while (!selectedMap.get(department.getId()).getObject() && department.getMasterDepartment() != null) {
                                    department = department.getMasterDepartment();
                                }

                                tree.updateBranch(department, target);
                            }

                            @Override
                            protected Component newLabelComponent(String id, IModel<Department> departmentIModel) {
                                return new Label(id, departmentIModel.getObject().getName());
                            }

                            @Override
                            protected MarkupContainer newLinkComponent(String id, final IModel<Department> departmentIModel) {
                                return isDisabled(departmentIModel)? new
                                        WebMarkupContainer(id) {
                                            @Override
                                            protected void onComponentTag(ComponentTag tag)
                                            {
                                                tag.setName("span");
                                                super.onComponentTag(tag);
                                            }
                                        }:
                                        new Link(id) {
                                            @Override
                                            public void onClick() {
                                                PageParameters parameters = new PageParameters();
                                                parameters.set(DepartmentEdit.PARAM_DEPARTMENT_ID, departmentIModel.getObject().getId());
                                                setResponsePage(DepartmentEdit.class, parameters);
                                            }
                                        };
                            }

                            @Override
                            protected boolean isClickable() {
                                return false;
                            }

                            @Override
                            protected void onClick(AjaxRequestTarget target) {
                                //select(getModelObject(), tree, target);
                            }

                            @Override
                            public boolean isEnabled() {
                                return true;//department == null || !getModelObject().getId().equals(department.getId());
                            }

                            @Override
                            protected boolean isSelected() {
                                return getModelObject().equals(department);
                                /*
                                Department department = getModelObject();

                                IModel<Department> model = provider.model(department);

                                try {
                                    return selected != null && selected.equals(model);
                                } finally {
                                    model.detach();
                                }
                                */
                            }

                            private boolean isDisabled(IModel<Department> departmentIModel) {
                                return (department != null && department.getId() == null) ||
                                        departmentIModel.getObject().equals(department);
                            }

                            /*
                            protected void select(Department department,
                                                  AbstractTree<Department> tree,
                                                  final AjaxRequestTarget target) {
                                if (selected != null) {
                                    tree.updateNode(selected.getObject(), target);

                                    selected.detach();
                                    selected = null;
                                }

                                selected = provider.model(department);

                                tree.updateNode(department, target);
                            }
                            */
                        };
			}
		};
        tree.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.renderCSSReference(THEME);
            }
        });

        add(tree);

        add(new Link("add") {

            @Override
            public void onClick() {
                PageParameters parameters = new PageParameters();
                if (department != null) {
                    parameters.set(DepartmentEdit.PARAM_MASTER_DEPARTMENT_ID, department.getId());
                } else if (filter.getOrganizationId() != null) {
                    parameters.set(OrganizationEdit.PARAM_ORGANIZATION_ID, filter.getOrganizationId());
                }
                setResponsePage(DepartmentEdit.class, parameters);
            }
        }.setVisible(isEnabledAction()));

        add(new AjaxButton("delete") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<Long> deleted = Lists.newArrayList();
                for (Map.Entry<Long, IModel<Boolean>> entry : selectedMap.entrySet()) {
                    if (entry.getValue().getObject()) {
                        deleted.add(entry.getKey());
                        try {
                            departmentBean.deleteDepartment(getDepartmentById(entry.getKey()));
                        } catch (ObjectNotFoundException e) {
                            log.error("Could not delete department " + entry.getKey(), e);
                        }
                    }
                }
                for (Long id : deleted) {
                    if (department != null && department.getMasterDepartment() != null &&
                            id.equals(department.getMasterDepartment().getId())) {
                        try {
                            department = getDepartmentById(department.getId());
                        } catch (ObjectNotFoundException e) {
                            log.error("Could not update department " + id, e);
                        }
                    }
                    selectedMap.remove(id);
                }
                provider.setRoots(new DepartmentIterator(departmentBean.getDepartments(filter)));
                target.add(tree);
            }

            private Department getDepartmentById(Long departmentId) throws ObjectNotFoundException {
                Department department = departmentBean.getDepartment(departmentId);
                if (department != null) {
                    return department;
                }
                throw new ObjectNotFoundException();
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }
        }.setVisible(isEnabledAction()));
    }

    private boolean isEnabledAction() {
        return department == null || department.getId() != null;
    }

    private IModel<Set<Department>> newStateModel() {
		return new AbstractReadOnlyModel<Set<Department>>()
		{
			@Override
			public Set<Department> getObject() {
				return state;
			}

			/**
			 * Super class doesn't detach - would be nice though.
			 */
			@Override
			public void detach() {
				((IDetachable)state).detach();
			}
		};
	}

    public Department getMasterDepartment() {
        return department != null? department.getMasterDepartment(): null;
    }
}
