package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.request.resource.ResourceReference;
import org.complitex.flexbuh.common.entity.HierarchicalTemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObjectIterator;
import org.complitex.flexbuh.personnel.entity.HierarchicalTemporalDomainObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wickettree.NestedTree;
import wickettree.content.CheckedFolder;
import wickettree.util.InverseSet;
import wickettree.util.ProviderSubset;

import javax.ejb.ObjectNotFoundException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pavel Sknar
 *         Date: 11.07.12 14:53
 */
public abstract class TreePanel<T extends HierarchicalTemporalDomainObject<T>> extends Panel {

    private static final Logger log = LoggerFactory.getLogger(TreePanel.class);

    private static ResourceReference THEME = new TreeTheme();

    private NestedTree<T> tree;

    private Map<Long, IModel<Boolean>> selectedMap = Maps.newHashMap();

    private HierarchicalTemporalDomainObjectProvider<T> provider = new HierarchicalTemporalDomainObjectProvider<>();

    private Set<T> state = new InverseSet<>(new ProviderSubset<>(provider));

    protected T currentItem;

    protected boolean enabled = true;

    public TreePanel(@NotNull String id, @Null T currentItem) {
        super(id);
        this.currentItem = currentItem;
    }

    protected void init() {

        List<T> filteredTDObjects = getFilteredTDObjects();
        log.debug("Current item: {}", currentItem);
        log.debug("Tree: {}", filteredTDObjects);
        provider.setRoots(new TemporalDomainObjectIterator<>(filteredTDObjects));

        tree = new NestedTree<T>("tree", provider, newStateModel()) {

            @Override
			protected org.apache.wicket.Component newContentComponent(String id, IModel<T> model) {

                return new CheckedFolder<T>(id, tree, model) {

                            @Override
                            protected IModel<Boolean> newCheckBoxModel(final IModel<T> model) {
                                IModel<Boolean> selectModel = new Model<>(false);
                                if (currentItem != null && currentItem.getId() == null
                                        && model.getObject().equals(currentItem.getMaster())) {
                                    selectModel.setObject(true);
                                }
                                selectedMap.put(model.getObject().getId(), selectModel);
                                return selectModel;
                            }

                            @Override
                            protected Component newCheckBox(String id, IModel<T> itemIModel) {
                                return super.newCheckBox(id, itemIModel).
                                        setEnabled(!isDisabled(itemIModel) && !itemIModel.getObject().isDeleted()).setOutputMarkupId(true);
                            }

                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                T item = getModelObject();

                                // search first ancestor with quux not set
                                while (!selectedMap.get(item.getId()).getObject() && item.getMaster() != null) {
                                    item = item.getMaster();
                                }

                                tree.updateBranch(item, target);
                            }

                            @Override
                            protected Component newLabelComponent(String id, final IModel<T> itemIModel) {
                                return new Label(id, getName(itemIModel.getObject())).
                                        add(new AttributeModifier("class", "") {
                                            @Override
                                            protected String newValue(String currentValue, String replacementValue) {

                                                String htmlClass = "";

                                                if (itemIModel.getObject().isDeleted()) {
                                                    htmlClass = "deleted ";
                                                }

                                                List<T> oldDepartment = getHistoryTDObjects(itemIModel.getObject().getId());

                                                htmlClass += oldDepartment == null || oldDepartment.size() != 0 &&
                                                        oldDepartment.get(0).getEntryIntoForceDate().equals(
                                                        itemIModel.getObject().getEntryIntoForceDate()) ? "" : "edited ";

                                                return htmlClass;
                                            }
                                }).setOutputMarkupId(true);
                            }

                            @Override
                            protected MarkupContainer newLinkComponent(String id, final IModel<T> itemIModel) {
                                return isDisabled(itemIModel)? new
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
                                                setEditItemResponsePage(itemIModel.getObject().getId());
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
                                return true;//item == null || !getModelObject().getId().equals(item.getId());
                            }

                            @Override
                            protected boolean isSelected() {
                                return getModelObject().equals(currentItem);
                                /*
                                Department item = getModelObject();

                                IModel<Department> model = provider.model(item);

                                try {
                                    return selected != null && selected.equals(model);
                                } finally {
                                    model.detach();
                                }
                                */
                            }

                            private boolean isDisabled(IModel<T> itemIModel) {
                                return (currentItem != null && currentItem.getId() == null) ||
                                        itemIModel.getObject().equals(currentItem);
                            }

                            /*
                            protected void select(Department item,
                                                  AbstractTree<Department> tree,
                                                  final AjaxRequestTarget target) {
                                if (selected != null) {
                                    tree.updateNode(selected.getObject(), target);

                                    selected.detach();
                                    selected = null;
                                }

                                selected = provider.model(item);

                                tree.updateNode(item, target);
                            }
                            */
                        }.setOutputMarkupId(true);
			}
        };
        tree.add(new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.renderCSSReference(THEME);
            }
        });
        tree.setOutputMarkupId(true);

        add(tree);

        add(new Link("add") {

            @Override
            public void onClick() {
                setAddNewItemResponsePage();
            }

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        }.setVisible(isVisibleAction()));

        add(new AjaxButton("delete") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<Long> deleted = Lists.newArrayList();
                for (Map.Entry<Long, IModel<Boolean>> entry : selectedMap.entrySet()) {
                    if (entry.getValue().getObject()) {
                        deleted.add(entry.getKey());
                        try {
                            deleteTDObject(entry.getKey());
                        } catch (ObjectNotFoundException e) {
                            log.error("Could not delete tree item " + entry.getKey(), e);
                        }
                    }
                }
                for (Long id : deleted) {
                    /*
                    if (item != null && item.getMasters().hasNext() &&
                            id.equals(item.getMasters().next().getId())) {
                        try {
                            item = getDepartmentById(item.getId());
                        } catch (ObjectNotFoundException e) {
                            log.error("Could not update tree item " + id, e);
                        }
                    }
                    */
                    selectedMap.remove(id);
                }
                provider.setRoots(new TemporalDomainObjectIterator<>(getFilteredTDObjects()));
                target.add(tree);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            public boolean isEnabled() {
                return isEnabledAction();
            }
        }.setVisible(isVisibleAction()));

        setOutputMarkupId(true);
    }

    private boolean isEnabledAction() {
        log.debug("isEnabledAction\n\tenabled: {}\n\ttree item: {}", enabled, currentItem);
        return enabled && (currentItem == null ||
                currentItem.getId() != null && currentItem.getCompletionDate() == null);
    }

    private boolean isVisibleAction() {
        return currentItem == null || currentItem.getId() != null;
    }

    private IModel<Set<T>> newStateModel() {
		return new AbstractReadOnlyModel<Set<T>>()
		{
			@Override
			public Set<T> getObject() {
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

    public T getMasterDepartment() {
        return currentItem != null? currentItem.getMaster(): null;
    }

    public void updateState(Date currentDate, boolean enabled) {

        this.enabled = enabled;

        provider.setRoots(new TemporalDomainObjectIterator<>(getFilteredTDObjects()));
    }

    abstract protected void deleteTDObject(Long id) throws ObjectNotFoundException;

    abstract protected List<T> getHistoryTDObjects(Long id);

    abstract protected List<T> getFilteredTDObjects();

    abstract protected String getName(T item);

    abstract protected void setAddNewItemResponsePage();

    abstract protected void setEditItemResponsePage(Long id);
}
