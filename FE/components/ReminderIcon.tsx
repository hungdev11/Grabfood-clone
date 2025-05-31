"use client";

import { Bell, Calendar, Clock, Mail, X } from "lucide-react";
import { useState, useRef, useEffect } from "react";
import { Button } from "./ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "./ui/dialog";
import { Input } from "./ui/input";
import { Textarea } from "./ui/textarea";
import { Checkbox } from "./ui/checkbox";
import { Label } from "./ui/label";
import { format } from "date-fns";
import {
  createReminder,
  getUserReminders,
  deleteReminder,
  deleteAllReminders,
  ReminderRequest,
  ReminderResponse,
} from "@/utils/apiReminder";
import { toast } from "@/components/ui/use-toast";

interface ReminderIconProps {
  userId: string | null;
  onTrigger?: () => void;
}

export function ReminderIcon({ userId, onTrigger }: ReminderIconProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [reminders, setReminders] = useState<ReminderResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  // Form state
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [reminderDate, setReminderDate] = useState("");
  const [reminderTime, setReminderTime] = useState("");
  const [emailEnabled, setEmailEnabled] = useState(true);

  const fetchReminders = async () => {
    if (!userId) return;

    try {
      setLoading(true);
      const response = await getUserReminders();
      setReminders(
        Array.isArray(response.data) ? response.data : [response.data]
      );
    } catch (error) {
      console.error("Failed to fetch reminders:", error);
      toast({
        title: "Error",
        description: "Failed to load reminders",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userId) fetchReminders();
  }, [userId]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);
  const handleDeleteReminder = async (id: string) => {
    try {
      await deleteReminder(id);
      fetchReminders();
      toast({
        title: "Success",
        description: "Reminder deleted successfully",
      });
    } catch (error) {
      console.error("Failed to delete reminder:", error);
      toast({
        title: "Error",
        description: "Failed to delete reminder",
        variant: "destructive",
      });
    }
  };

  const handleDeleteAllReminders = async () => {
    if (!userId) return;

    try {
      await deleteAllReminders();
      fetchReminders();
      toast({
        title: "Success",
        description: "All reminders deleted successfully",
      });
    } catch (error) {
      console.error("Failed to delete all reminders:", error);
      toast({
        title: "Error",
        description: "Failed to delete all reminders",
        variant: "destructive",
      });
    }
  };
  const handleCreateReminder = async () => {
    if (!title || !reminderDate || !reminderTime) {
      toast({
        title: "Missing information",
        description: "Please fill in all required fields",
        variant: "destructive",
      });
      return;
    }

    // Parse date and time components
    const [year, month, day] = reminderDate.split("-").map(Number);
    const [hours, minutes] = reminderTime.split(":").map(Number);

    // Create date in local time
    const reminderDateTime = new Date(year, month - 1, day, hours, minutes);

    // For validation only - create a current date to compare
    const now = new Date();

    // Check if date is in the past
    if (reminderDateTime < now) {
      toast({
        title: "Invalid date",
        description: "Reminder time cannot be in the past",
        variant: "destructive",
      });
      return;
    }

    // Calculate the UTC time that corresponds to the local time the user selected
    // By adding the timezone offset to the local time values
    const timezoneOffsetMs = reminderDateTime.getTimezoneOffset() * 60 * 1000;
    const utcTime = new Date(reminderDateTime.getTime() - timezoneOffsetMs);

    // Log both times for debugging
    console.log("Local time selected:", reminderDateTime.toString());
    console.log("UTC time being sent:", utcTime.toISOString());

    const reminderRequest: ReminderRequest = {
      title,
      description,
      reminderTime: utcTime.toISOString(), // Send UTC time but compensated for timezone
      emailEnabled,
    };

    try {
      await createReminder(reminderRequest);
      fetchReminders();
      setDialogOpen(false);
      resetForm();
      toast({
        title: "Success",
        description: "Reminder created successfully",
      });
    } catch (error) {
      console.error("Failed to create reminder:", error);
      toast({
        title: "Error",
        description: "Failed to create reminder",
        variant: "destructive",
      });
    }
  };

  const resetForm = () => {
    setTitle("");
    setDescription("");
    setReminderDate("");
    setReminderTime("");
    setEmailEnabled(true);
  };

  const formatReminderTime = (isoString: string) => {
    const date = new Date(isoString);
    return format(date, "dd/MM/yyyy HH:mm");
  };

  const getPendingRemindersCount = () => {
    return reminders.filter((reminder) => !reminder.isProcessed).length;
  };

  return (
    <div className="relative" ref={menuRef}>
      {" "}
      <button onClick={() => setIsOpen(!isOpen)} className="relative">
        <Clock className="w-6 h-6 text-gray-700" />{" "}
        {getPendingRemindersCount() > 0 && (
          <span className="absolute top-0 right-0 block w-2 h-2 rounded-full bg-red-500"></span>
        )}
      </button>
      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg overflow-hidden z-50">
          <div className="p-3 bg-green-500 text-white flex justify-between items-center">
            <h3 className="font-semibold">Reminders</h3>
            <Button
              variant="ghost"
              size="sm"
              className="text-white hover:text-white hover:bg-green-600"
              onClick={() => setDialogOpen(true)}
            >
              + New
            </Button>
          </div>

          <div className="max-h-96 overflow-y-auto">
            {loading ? (
              <div className="p-4 text-center text-gray-500">Loading...</div>
            ) : reminders.length === 0 ? (
              <div className="p-4 text-center text-gray-500">
                No reminders found
              </div>
            ) : (
              reminders.map((reminder) => (
                <div
                  key={reminder.id}
                  className={`p-3 border-b hover:bg-gray-50 flex justify-between items-start ${
                    reminder.isProcessed ? "bg-gray-100" : ""
                  }`}
                >
                  <div className="flex-1">
                    <div className="font-medium">{reminder.title}</div>
                    <div className="text-sm text-gray-600">
                      {reminder.description}
                    </div>
                    <div className="text-xs text-gray-500 flex gap-1 mt-1">
                      <Clock className="h-3 w-3" />
                      {formatReminderTime(reminder.reminderTime)}
                    </div>
                    {reminder.isProcessed && (
                      <span className="text-xs text-green-500">Processed</span>
                    )}
                  </div>{" "}
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-6 w-6"
                    onClick={() => handleDeleteReminder(reminder.id)}
                  >
                    <X className="h-4 w-4" />
                  </Button>
                </div>
              ))
            )}
          </div>

          {reminders.length > 0 && (
            <div className="p-2 bg-gray-50 border-t">
              <Button
                variant="ghost"
                size="sm"
                className="w-full text-red-500 hover:text-red-600 hover:bg-red-50"
                onClick={handleDeleteAllReminders}
              >
                Delete All Reminders
              </Button>
            </div>
          )}
        </div>
      )}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create Reminder</DialogTitle>
          </DialogHeader>

          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="title">Title</Label>
              <Input
                id="title"
                placeholder="Reminder title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Description (optional)</Label>
              <Textarea
                id="description"
                placeholder="Add details about this reminder"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="date">Date</Label>
                <div className="relative">
                  <Calendar className="absolute left-3 top-2.5 h-4 w-4 text-gray-500" />
                  <Input
                    id="date"
                    type="date"
                    className="pl-10"
                    value={reminderDate}
                    onChange={(e) => setReminderDate(e.target.value)}
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="time">Time</Label>
                <div className="relative">
                  <Clock className="absolute left-3 top-2.5 h-4 w-4 text-gray-500" />
                  <Input
                    id="time"
                    type="time"
                    className="pl-10"
                    value={reminderTime}
                    onChange={(e) => setReminderTime(e.target.value)}
                  />
                </div>
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Checkbox
                id="email"
                checked={emailEnabled}
                onCheckedChange={(checked) =>
                  setEmailEnabled(checked as boolean)
                }
              />
              <Label
                htmlFor="email"
                className="flex items-center gap-1 text-sm"
              >
                <Mail className="h-4 w-4" />
                Send email notification
              </Label>
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleCreateReminder}>Create Reminder</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
