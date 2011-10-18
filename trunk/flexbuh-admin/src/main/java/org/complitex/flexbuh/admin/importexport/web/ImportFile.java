package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateControlService;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateFOService;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateXSDService;
import org.complitex.flexbuh.admin.importexport.service.ImportTemplateXSLService;
import org.complitex.flexbuh.service.ImportListener;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 10:09
 */
public class ImportFile extends TemplatePage {

	private final static Logger log = LoggerFactory.getLogger(ImportFile.class);

	@EJB
	private ImportTemplateFOService importTemplateFOService;

	@EJB
	private ImportTemplateXSDService importTemplateXSDService;

	@EJB
	private ImportTemplateXSLService importTemplateXSLService;

	@EJB
	private ImportTemplateControlService importTemplateControlService;

	@SuppressWarnings("unchecked")
    public ImportFile() {

		final WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

		final IModel<List<DataFile>> dataFileModel = new ListModel<>();

        container.add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        container.add(form);

		//Dictionary types
        final CheckBoxMultipleChoice<DataFile> dataFiles =
				new CheckBoxMultipleChoice<>("dataFiles", dataFileModel, Arrays.asList(DataFile.values()),
						new IChoiceRenderer<DataFile>() {

							@Override
							public Object getDisplayValue(DataFile object) {
								return getString(object.toString());
							}

							@Override
							public String getIdValue(DataFile object, int index) {
								return object.toString();
							}
						});

		form.add(dataFiles);

        //Кнопка импортировать
        Button process = new Button("process") {
            @Override
            public void onSubmit() {
				log.debug("Submit process");

				log.debug("Selected objects: {}", dataFileModel.getObject());

				DictionaryImportListener importListener = new DictionaryImportListener();

                final Long sessionId = getSessionId(true);

				for (DataFile dataFile : dataFileModel.getObject()) {
					switch (dataFile) {
						case CONTROL:
							log.debug("start import control");
							importListener.addProcessingCountFiles(importTemplateControlService.listImportFiles().length);
							importTemplateControlService.processFiles(sessionId, importListener, null, null);
							break;
						case FO:
							log.debug("start import fo");
							importListener.addProcessingCountFiles(importTemplateFOService.listImportFiles().length);
							importTemplateFOService.processFiles(sessionId, importListener, null, null);
							break;
						case XSD:
							log.debug("start import xsd");
							importListener.addProcessingCountFiles(importTemplateXSDService.listImportFiles().length);
							importTemplateXSDService.processFiles(sessionId, importListener, null, null);
							break;
						case XSL:
							log.debug("start import xsl");
							importListener.addProcessingCountFiles(importTemplateXSLService.listImportFiles().length);
							importTemplateXSLService.processFiles(sessionId, importListener, null, null);
							break;
					}
				}

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

				log.debug("import file: {}, listener status: {}, count completed: {}, count canceled: {}",
						new Object[]{listener.currentImportFile(), listener.getStatus(), listener.getCountCompleted(), listener.getCountCanceled()});

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

		public void addProcessingCountFiles(int count) {
			countTotal = countTotal + count;
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

	private enum DataFile {
		XSD("xsd"), XSL("xsl"), FO("fo"), CONTROL("control");

		private String fileName;

		private DataFile(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public String toString() {
			return fileName;
		}
	}
}
