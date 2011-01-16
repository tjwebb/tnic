/**
 * @author Travis Webb (travis@traviswebb.com)
 */
var tnic = (function () {
    var baseUrl = "http://" + window.location.host + "/editor_server?";
    function addBodyElem (name, id) {
        var elem = document.createElement(name);
        elem.id = id;
        document.body.appendChild(elem);
    }

    function includeCss (href, frame) {
        var css = document.createElement('link');
        css.setAttribute('rel' , 'stylesheet');
        css.setAttribute('href', href);
        css.setAttribute('type', 'text/css');
        if (frame) {
            document.getElementsByTagName('iframe')[0].appendChild(css);
            return;
        }
        document.getElementsByTagName('head')[0].appendChild(css);
    }

    this.editor = (function () {
        var menuOpen          = false;
        var currentFile       = null;
        var state             = 'editor';
        var editor            = null;
        var cli               = null;
        var editorElem        = null;
        var editorWrapperElem = null;
        //var editorFrame       = [ ];
        //var cliWrapperElem    = null;
        //var hiddenKeyField    = null;

        var editorKeyHandlers = {
            /* Esc */ '27' : function () {
                if (!menuOpen) {
                    $("#menu").jGrowl(
                        "Current File: <div id='currentFile'></div><br/>" +
                        "<a href='javascript:tnic.editor.newFile();'>New</a> " +
                        "<a href='javascript:tnic.editor.open();'>Open</a> " +
                        "<a href='javascript:tnic.editor.save();'>Save</a> ", {
                        sticky: true,
                        close : function () {
                            menuOpen = false;
                        }
                    });
                    menuOpen = true;
                }
                //state = 'hidden';
         //       console.log(hiddenKeyField);
         //       hiddenKeyField.focus();
            }
        };
        /*
        var cliKeyHandlers = {
            /* Enter  '13' : function () {
                submitCliCommand(cli.val());
                cli.val('');
                state = 'hidden';
                hiddenKeyField.focus();
            },
            /* Esc    '27' : function () {
                state = 'hidden';
                hiddenKeyField.focus();
                cli.val('');
            }
        };
        var hiddenKeyHandlers = {
            /* :      '58' : function () {
                state = 'cli';
                cli.focus();
                cli.val(':');
            },
            /* i      '73' : function () {
                state = 'editor';
                hiddenKeyField.val('');
                editor.focus();
                console.log('insert');
            }
        };

        function submitCliCommand (cmd) {
            console.log('cli command: '+ cmd);
        }

        */
        function handleKeyEvents (e) {
            var key = e.keyCode;
            var handler = eval(state + "KeyHandlers");
            if (state == "editor") {
                window.focus();
            }
            if (key in handler) handler[e.keyCode]();
        }
        /*

        function initCLI (elem) {
            /* include cli stylesheet 
            includeCss('css/clicolors.css');
        }

        function applyKeyHandlers () {
            hiddenKeyField = $('#hidden_key');
            hiddenKeyField.focus();
            hiddenKeyField.keypress(function (e) {
                handleKeyEvents(e);
            });
            hiddenKeyField.keyup(function (e) {
                handleKeyEvents(e);
            });
            cli.keypress(function (e) {
                handleKeyEvents(e);
            });
            cli.keyup(function (e) {
                handleKeyEvents(e);
            });
        }

        function applyNullHandlers () {
            function reFocus () {
                state = 'hidden';
                window.focus();
            }
            var win = $(window);
            win.resize(applyWrapperDimensions);
            win.click(reFocus);
        }
        */

        function applyWrapperDimensions () {
            /* calculate dimensions for cli wrapper */
            /*
            var xCli = (function () {
                cliWrapperElem.css('width' , window.innerWidth - 2);
                cliWrapperElem.css('height', '20px');
                return 20;
            })();
            */

            /* calculate dimensions for editor wrapper */
            (function () {
                var xMax = window.innerWidth;
                var xLN = $("div.CodeMirror-line-numbers:first").outerWidth(true);
                var wrapWidth = xMax - xLN - 10;

                var yMax = window.innerHeight;
                var yLNSingle = 
                    $("div.CodeMirror-line-numbers:first > div:first").outerHeight(true);
                var numLines = Math.floor(yMax / yLNSingle) - 1;
                var wrapHeight = numLines * yLNSingle;// - xCli;

                editorWrapperElem.css('width' , wrapWidth);
                editorWrapperElem.css('height', wrapHeight);
            })();

            editorWrapperElem.css('vertical-align', 'top');
        }
        return {
        initEditor : function (argv) {
            editorWrapperElem = $('#'+ argv.editorWrapper);
            cliWrapperElem    = $('#'+ argv.cliWrapper);
            cli               = $('#'+ argv.cli);
            editorElem        = $('#'+ argv.editor);
            editor = new CodeMirror(document.getElementById(argv.editor), {
                path        : "js/",
                parserfile  : [ "tokenizejavascript.js", "parsejavascript.js" ],
                stylesheet  : "css/jscolors.css",
                lineNumbers : "true",
                height      : "100%",
                textWrapping: true,
                indentUnit  : 4,
                iframeClass : "editorFrame",
                onLoad      : function (editor) {
                    editor.focus();
                    editor.grabKeys(handleKeyEvents, function (key) {
                        if (key in editorKeyHandlers) {
                            handleKeyEvents(key);
                            return true;
                        }
                        return false;
                    });
                }
            });
            //addBodyElem('input', 'hidden_key');
//            initCLI();
            applyWrapperDimensions();
//            applyKeyHandlers();
//            applyNullHandlers();
        },
        save : function (newFile) {
            var filename = $('#open_file').val()
            if (newFile) {
                editor.setCode('');
                currentFile = filename;
            }
            else if (currentFile === null) return;
            var url = baseUrl + "f=" + currentFile;
            $.ajax({
                url : url,
                type : "POST",
                data : { c : editor.getCode() },
                success : function (msg) {
                    $('#currentFile').html(currentFile);
                }
            });
        },
        open : function () {
            $('#menu').jGrowl(
                "<input id='open_file' /><br/>" +
                "<a href=javascript:tnic.editor.load();>Load</a>", {
                    stricky : true
                }
            );
            $('#open_file').focus();
        },
        load : function () {
            var filename = $('#open_file').val()
            $('#open_file').val('');
            var url = baseUrl + "f=" + filename;
            $.ajax({
                url : url,
                success : function (msg) {
                    if (msg == "File Not Found") {
                        currentFile = filename;
                    }
                    editor.setCode(msg);
                    $('#currentFile').html(currentFile);
                }
            });
        },
        newFile : function () {
            $('#menu').jGrowl(
                "<input id='open_file' /><br/>" +
                "<a href=javascript:tnic.editor.save(true);>New File</a>", {
                    stricky : true
                }
            );
        }
        };
    })();
    return {
        "editor" : this.editor
    };
})();
