package org.complitex.flexbuh.admin.importexport.web;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.flexbuh.admin.importexport.service.ImportDictionaryService;
import org.complitex.flexbuh.admin.importexport.service.ImportListener;
import org.complitex.flexbuh.entity.dictionary.DictionaryType;
import org.complitex.flexbuh.service.dictionary.DictionaryTypeBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.08.11 17:26
 */
public class ImportDictionary extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(ImportDictionary.class);

	@EJB
    private DictionaryTypeBean dictionaryTypeService;

	@EJB
	private ImportDictionaryService importDictionaryService;

	@SuppressWarnings("unchecked")

    public ImportDictionary() {

		final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

		final IModel<List<DictionaryType>> dictionaryModel = new ListModel<DictionaryType>();

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

		//Dictionary types
        final CheckBoxMultipleChoice<DictionaryType> dictionaryTypes =
				new CheckBoxMultipleChoice<DictionaryType>("dictionaryTypes", dictionaryModel, dictionaryTypeService.readAll(),
						new IChoiceRenderer<DictionaryType>() {

							@Override
							public Object getDisplayValue(DictionaryType object) {
								return object.getDefaultName();
							}

							@Override
							public String getIdValue(DictionaryType object, int index) {
								return object.getCode();
							}
						});

		form.add(dictionaryTypes);

		//Begin Date
		final IModel<Date> beginDateModel = new Model<Date>();
		DateTextField beginDate = new DateTextField("beginDate", beginDateModel);
        //dateTextField.add(new DatePicker("beginDate"));
        form.add(beginDate);

        //End Date
		final IModel<Date> endDateModel = new Model<Date>();
		DateTextField endDate = new DateTextField("endDate", endDateModel);
		//DatePicker<Date> endDate = new DatePicker<Date>("endDate", endDateModel);
        form.add(endDate);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
				log.debug("Submit process");

				List<String> fileNames = Lists.newArrayList();
				log.debug("Selected objects: {}", dictionaryModel.getObject());

				log.debug("Begin date {}, end date {}", beginDateModel.getObject(), endDateModel.getObject());

				for (DictionaryType dictionaryType : dictionaryModel.getObject()) {
					fileNames.addAll(dictionaryType.getFileNames());
				}

				DictionaryImportListener importListener = new DictionaryImportListener(fileNames.size());
				importDictionaryService.processFiles(importListener, fileNames, beginDateModel.getObject(), endDateModel.getObject());

				container.add(newTimer(importListener));
            }

            @Override
            public boolean isVisible() {
                return true; //listeners.isEmpty();
            }
        };
        form.add(process);

        //Ошибки
        container.add(new Label("error", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return null;//addressImportService.getErrorMessage();
            }
        }){
            @Override
            public boolean isVisible() {
                return false;//addressImportService.isError();
            }
        });
    }

    private AjaxSelfUpdatingTimerBehavior newTimer(final DictionaryImportListener listener){

        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)){
            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {

				log.debug("listener status: {}, count completed: {}, count canceled: {}",
						new Object[]{listener.getStatus(), listener.getCountCompleted(), listener.getCountCanceled()});

				if (listener.isEnded()) {
					info(getStringFormat("complete", listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
					stop();
				} else {
					info(getStringFormat("processing", listener.currentImportFile(), listener.getCountCompleted(), listener.getCountCanceled(), listener.getCountTotal()));
				}
            }
        };
    }

	private class DictionaryImportListener implements ImportListener, Serializable {

		private String fileName = "";
		private Integer countCompleted = 0;
		private Integer countCanceled = 0;
		private Integer countTotal = 0;
		
		private Status status = null;

		public DictionaryImportListener(Integer countTotal) {
			this.countTotal = countTotal;
		}

		@Override
		public void begin() {
			status = Status.PROCESSING;
		}

		@Override
		public void completed() {
			if (countCanceled > 0) {
				status = Status.PROCESSED_WITH_ERROR;
			} else {
				status = Status.PROCESSED;
			}
		}

		@Override
		public void completedWithError() {
			status = Status.PROCESSED_WITH_ERROR;
		}

		@Override
		public void cancel() {
			status = Status.CANCELED;
		}
		
		public String currentImportFile() {
			synchronized (fileName) {
				return fileName;
			}
		}

		public long getCountCompleted() {
			synchronized (countCompleted) {
				return countCompleted;
			}
		}

		public long getCountCanceled() {
			synchronized (countCanceled) {
				return countCanceled;
			}
		}

		public long getCountTotal() {
			return countTotal;
		}

		public Status getStatus() {
			return status;
		}

		public boolean isEnded() {
			return Status.PROCESSED.equals(status)
					|| Status.PROCESSED_WITH_ERROR.equals(status)
					|| Status.CANCELED.equals(status);
		}

		@Override
		public ImportListener getChildImportListener(Object o) {
			synchronized (fileName) {
				fileName = ((File)o).getName();
			}
			return new ImportListener() {
				@Override
				public ImportListener getChildImportListener(Object o) {
					throw new RuntimeException("Do not using");
				}

				@Override
				public void begin() {
					
				}

				@Override
				public void completed() {
					synchronized (fileName) {
						fileName= "";
					}
					synchronized (countCompleted) {
						countCompleted ++;
					}
				}

				@Override
				public void completedWithError() {
				}

				@Override
				public void cancel() {
					synchronized (fileName) {
						fileName = "";
					}
					synchronized (countCanceled) {
						countCanceled ++;
					}
				}
			};
		}
	}
	
	private enum Status {
		PROCESSING, PROCESSED, PROCESSED_WITH_ERROR, CANCELED
	}

}
